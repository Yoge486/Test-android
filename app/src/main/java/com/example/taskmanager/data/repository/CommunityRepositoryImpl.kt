package com.example.taskmanager.data.repository

import com.example.taskmanager.data.model.CommunityDto
import com.example.taskmanager.data.model.MessageDto
import com.example.taskmanager.domain.model.Community
import com.example.taskmanager.domain.model.Message
import com.example.taskmanager.domain.repository.CommunityRepository
import com.example.taskmanager.domain.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : CommunityRepository {

    override suspend fun getCommunities(): Flow<Resource<List<Community>>> = flow {
        emit(Resource.Loading())
        try {
            val communityDtos = supabaseClient.postgrest["communities"]
                .select()
                .decodeList<CommunityDto>()
            emit(Resource.Success(communityDtos.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch communities"))
        }
    }

    override suspend fun getMessages(communityId: String): Flow<Resource<List<Message>>> = flow {
        emit(Resource.Loading())
        try {
            val messageDtos = supabaseClient.postgrest["messages"]
                .select {
                    filter {
                        eq("community_id", communityId)
                    }
                }
                .decodeList<MessageDto>()
            emit(Resource.Success(messageDtos.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch messages"))
        }
    }

    override suspend fun sendMessage(communityId: String, content: String): Resource<Unit> {
        return try {
            val user = supabaseClient.auth.currentUserOrNull()
                ?: return Resource.Error("User not authenticated")

            val newMessage = MessageDto(
                communityId = communityId,
                senderId = user.id,
                content = content
            )
            supabaseClient.postgrest["messages"].insert(newMessage)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send message")
        }
    }

    override suspend fun createCommunity(name: String, description: String?, isOrgWide: Boolean): Resource<Unit> {
        return try {
            val communityData = kotlinx.serialization.json.buildJsonObject {
                put("name", kotlinx.serialization.json.JsonPrimitive(name))
                if (description != null) {
                    put("description", kotlinx.serialization.json.JsonPrimitive(description))
                }
                put("is_org_wide", kotlinx.serialization.json.JsonPrimitive(isOrgWide))
            }
            supabaseClient.postgrest["communities"].insert(communityData)
            Resource.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("CommunityRepo", "Create community failed: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to create community")
        }
    }
}

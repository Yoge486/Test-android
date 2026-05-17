package com.example.taskmanager.data.repository

import com.example.taskmanager.data.model.ProfileDto
import com.example.taskmanager.domain.model.Profile
import com.example.taskmanager.domain.repository.ProfileRepository
import com.example.taskmanager.domain.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ProfileRepository {

    override suspend fun getProfile(): Flow<Resource<Profile>> = flow {
        emit(Resource.Loading())
        try {
            val user = supabaseClient.auth.currentUserOrNull()
            if (user == null) {
                emit(Resource.Error("User not authenticated"))
                return@flow
            }
            
            val profileDto = supabaseClient.postgrest["profiles"]
                .select {
                    filter {
                        eq("id", user.id)
                    }
                }.decodeSingleOrNull<ProfileDto>()

            if (profileDto != null) {
                emit(Resource.Success(profileDto.toDomain(email = user.email)))
            } else {
                emit(Resource.Error("Profile not found"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch profile"))
        }
    }

    override suspend fun updateProfile(fullName: String, department: String): Resource<Unit> {
        return try {
            val user = supabaseClient.auth.currentUserOrNull()
                ?: return Resource.Error("User not authenticated")

            val updateData = mapOf(
                "full_name" to fullName,
                "department" to department
            )

            supabaseClient.postgrest["profiles"].update(updateData) {
                filter {
                    eq("id", user.id)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile")
        }
    }

    override suspend fun getTeamMembers(): Flow<Resource<List<Profile>>> = flow {
        emit(Resource.Loading())
        try {
            val profileDtos = supabaseClient.postgrest["profiles"]
                .select()
                .decodeList<ProfileDto>()

            emit(Resource.Success(profileDtos.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch team members"))
        }
    }
}

package com.example.taskmanager.domain.repository

import com.example.taskmanager.domain.model.Community
import com.example.taskmanager.domain.model.Message
import com.example.taskmanager.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    suspend fun getCommunities(): Flow<Resource<List<Community>>>
    suspend fun getMessages(communityId: String): Flow<Resource<List<Message>>>
    suspend fun sendMessage(communityId: String, content: String): Resource<Unit>
    suspend fun createCommunity(name: String, description: String?, isOrgWide: Boolean): Resource<Unit>
}

package com.example.taskmanager.domain.repository

import com.example.taskmanager.domain.model.Profile
import com.example.taskmanager.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(): Flow<Resource<Profile>>
    suspend fun updateProfile(fullName: String, department: String): Resource<Unit>
    suspend fun getTeamMembers(): Flow<Resource<List<Profile>>>
}

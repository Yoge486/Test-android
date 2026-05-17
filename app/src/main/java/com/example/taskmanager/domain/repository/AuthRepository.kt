package com.example.taskmanager.domain.repository

import com.example.taskmanager.domain.model.UserRole
import com.example.taskmanager.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    
    val sessionStatus: Flow<Boolean>

    suspend fun login(email: String, password: String): Resource<Unit>

    suspend fun register(email: String, password: String, role: UserRole): Resource<Unit>

    suspend fun logout(): Resource<Unit>
    
    suspend fun checkSession(): Boolean

    suspend fun submitPasswordResetRequest(email: String, reason: String): Resource<Unit>
    
    suspend fun checkPasswordResetStatus(email: String): Resource<String>
    
    suspend fun overridePassword(email: String, newPassword: String): Resource<Unit>
    
    suspend fun getPendingResets(): Resource<List<com.example.taskmanager.domain.model.PasswordResetRequest>>
    
    suspend fun updateResetRequestStatus(id: String, status: String): Resource<Unit>
}

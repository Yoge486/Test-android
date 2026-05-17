package com.example.taskmanager.domain.model

data class Profile(
    val id: String,
    val email: String? = null,
    val fullName: String?,
    val role: UserRole,
    val department: String?,
    val teamId: String?,
    val managerId: String?,
    val createdAt: String?
)

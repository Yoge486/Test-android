package com.example.taskmanager.data.model

import com.example.taskmanager.domain.model.Profile
import com.example.taskmanager.domain.model.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("id") val id: String,
    @SerialName("full_name") val fullName: String?,
    @SerialName("role") val role: String?,
    @SerialName("department") val department: String?,
    @SerialName("team_id") val teamId: String?,
    @SerialName("manager_id") val managerId: String?,
    @SerialName("created_at") val createdAt: String?
) {
    fun toDomain(email: String? = null): Profile {
        return Profile(
            id = id,
            email = email,
            fullName = fullName,
            role = try { UserRole.valueOf(role ?: "EMPLOYEE") } catch (e: Exception) { UserRole.EMPLOYEE },
            department = department,
            teamId = teamId,
            managerId = managerId,
            createdAt = createdAt
        )
    }
}

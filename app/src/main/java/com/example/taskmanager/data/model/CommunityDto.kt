package com.example.taskmanager.data.model

import com.example.taskmanager.domain.model.Community
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommunityDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("team_id") val teamId: String? = null,
    @SerialName("is_org_wide") val isOrgWide: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
) {
    fun toDomain(): Community {
        return Community(
            id = id,
            name = name,
            description = description,
            teamId = teamId,
            isOrgWide = isOrgWide,
            createdAt = createdAt
        )
    }
}

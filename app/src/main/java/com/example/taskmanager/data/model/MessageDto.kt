package com.example.taskmanager.data.model

import com.example.taskmanager.domain.model.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    @SerialName("id") val id: String? = null,
    @SerialName("community_id") val communityId: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("content") val content: String,
    @SerialName("created_at") val createdAt: String? = null
) {
    fun toDomain(): Message {
        return Message(
            id = id ?: "",
            communityId = communityId,
            senderId = senderId,
            content = content,
            createdAt = createdAt
        )
    }
}

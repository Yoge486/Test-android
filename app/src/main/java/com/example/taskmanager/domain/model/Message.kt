package com.example.taskmanager.domain.model

data class Message(
    val id: String,
    val communityId: String,
    val senderId: String,
    val content: String,
    val createdAt: String?
)

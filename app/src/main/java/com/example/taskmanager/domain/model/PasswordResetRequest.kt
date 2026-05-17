package com.example.taskmanager.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequest(
    val id: String? = null,
    @SerialName("user_email") val userEmail: String,
    val reason: String,
    val status: String = "PENDING"
)

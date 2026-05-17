package com.example.taskmanager.domain.model

data class Community(
    val id: String,
    val name: String,
    val description: String?,
    val teamId: String?,
    val isOrgWide: Boolean,
    val createdAt: String?
)

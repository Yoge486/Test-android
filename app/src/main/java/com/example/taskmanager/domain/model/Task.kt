package com.example.taskmanager.domain.model

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val priority: TaskPriority,
    val status: TaskStatus,
    val category: String?,
    val dueDate: String?,
    val assigneeId: String?,
    val creatorId: String?,
    val teamId: String?,
    val createdAt: String?
)

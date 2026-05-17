package com.example.taskmanager.domain.repository

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun getTasksForUser(): Flow<Resource<List<Task>>>
    suspend fun createTask(
        title: String,
        description: String?,
        priority: String,
        dueDate: String?,
        assigneeId: String? = null
    ): Resource<Unit>
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Resource<Unit>
}

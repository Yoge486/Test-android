package com.example.taskmanager.domain.usecase.task

import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.domain.repository.TaskRepository
import com.example.taskmanager.domain.util.Resource
import javax.inject.Inject

class UpdateTaskStatusUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: String, status: TaskStatus): Resource<Unit> {
        return repository.updateTaskStatus(taskId, status)
    }
}

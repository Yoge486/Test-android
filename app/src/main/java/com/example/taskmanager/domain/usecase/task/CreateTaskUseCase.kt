package com.example.taskmanager.domain.usecase.task

import com.example.taskmanager.domain.repository.TaskRepository
import com.example.taskmanager.domain.util.Resource
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        priority: String,
        dueDate: String?,
        assigneeId: String? = null
    ): Resource<Unit> {
        if (title.isBlank()) {
            return Resource.Error("Task title cannot be blank")
        }
        return repository.createTask(title, description, priority, dueDate, assigneeId)
    }
}

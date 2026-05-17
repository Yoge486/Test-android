package com.example.taskmanager.data.repository

import com.example.taskmanager.data.model.TaskDto
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.domain.repository.TaskRepository
import com.example.taskmanager.domain.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : TaskRepository {

    override suspend fun getTasksForUser(): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading())
        try {
            val user = supabaseClient.auth.currentUserOrNull()
            val userId = user?.id ?: return@flow emit(Resource.Error("Not authenticated"))
            
            val taskDtos = supabaseClient.postgrest["tasks"]
                .select {
                    filter {
                        // fetch tasks created by OR assigned to the user
                        or {
                            eq("assignee_id", userId)
                            eq("creator_id", userId)
                        }
                    }
                }.decodeList<TaskDto>()
            
            emit(Resource.Success(taskDtos.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch tasks"))
        }
    }

    override suspend fun createTask(
        title: String,
        description: String?,
        priority: String,
        dueDate: String?,
        assigneeId: String?
    ): Resource<Unit> {
        return try {
            val user = supabaseClient.auth.currentUserOrNull()
            if (user == null) {
                return Resource.Error("User not authenticated")
            }
            
            val newTask = TaskDto(
                title = title,
                description = description,
                priority = priority,
                status = TaskStatus.TO_DO.name,
                dueDate = dueDate,
                creatorId = user.id,
                assigneeId = assigneeId ?: user.id
            )
            
            supabaseClient.postgrest["tasks"].insert(newTask)
            Resource.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("TaskRepo", "Create task failed: ${e::class.simpleName}: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to create task")
        }
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Resource<Unit> {
        return try {
            val updateData = mapOf("status" to status.name)
            supabaseClient.postgrest["tasks"].update(updateData) {
                filter {
                    eq("id", taskId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update task status")
        }
    }
}

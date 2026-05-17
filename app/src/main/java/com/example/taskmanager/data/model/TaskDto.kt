package com.example.taskmanager.data.model

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.model.TaskPriority
import com.example.taskmanager.domain.model.TaskStatus
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class TaskDto(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("id") val id: String? = null,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("priority") val priority: String = "MEDIUM",
    @SerialName("status") val status: String = "TO_DO",
    @SerialName("category") val category: String? = null,
    @SerialName("due_date") val dueDate: String? = null,
    @SerialName("assignee_id") val assigneeId: String? = null,
    @SerialName("creator_id") val creatorId: String? = null,
    @SerialName("team_id") val teamId: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("created_at") val createdAt: String? = null
) {
    fun toDomain(): Task {
        return Task(
            id = id ?: "",
            title = title,
            description = description,
            priority = try { TaskPriority.valueOf(priority) } catch(e: Exception) { TaskPriority.MEDIUM },
            status = try { TaskStatus.valueOf(status) } catch(e: Exception) { TaskStatus.TO_DO },
            category = category,
            dueDate = dueDate,
            assigneeId = assigneeId,
            creatorId = creatorId,
            teamId = teamId,
            createdAt = createdAt
        )
    }
}


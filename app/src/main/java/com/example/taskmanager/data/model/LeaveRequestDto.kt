package com.example.taskmanager.data.model

import com.example.taskmanager.domain.model.LeaveRequest
import com.example.taskmanager.domain.model.LeaveStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaveRequestDto(
    @SerialName("id") val id: String? = null,
    @SerialName("employee_id") val employeeId: String? = null,
    @SerialName("leave_type") val leaveType: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("reason") val reason: String? = null,
    @SerialName("status") val status: String = "PENDING",
    @SerialName("leader_comment") val leaderComment: String? = null,
    @SerialName("hr_comment") val hrComment: String? = null,
    @SerialName("created_at") val createdAt: String? = null
) {
    fun toDomain(): LeaveRequest {
        return LeaveRequest(
            id = id ?: "",
            employeeId = employeeId,
            leaveType = leaveType,
            startDate = startDate,
            endDate = endDate,
            reason = reason,
            status = try { LeaveStatus.valueOf(status) } catch (e: Exception) { LeaveStatus.PENDING },
            leaderComment = leaderComment,
            hrComment = hrComment,
            createdAt = createdAt
        )
    }
}

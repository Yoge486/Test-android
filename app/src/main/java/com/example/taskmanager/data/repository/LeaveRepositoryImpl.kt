package com.example.taskmanager.data.repository

import com.example.taskmanager.data.model.LeaveRequestDto
import com.example.taskmanager.domain.model.LeaveRequest
import com.example.taskmanager.domain.repository.LeaveRepository
import com.example.taskmanager.domain.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LeaveRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : LeaveRepository {

    override suspend fun submitLeaveRequest(
        leaveType: String,
        startDate: String,
        endDate: String,
        reason: String
    ): Resource<Unit> {
        return try {
            val user = supabaseClient.auth.currentUserOrNull()
                ?: return Resource.Error("Not authenticated")

            val dto = LeaveRequestDto(
                employeeId = user.id,
                leaveType = leaveType,
                startDate = startDate,
                endDate = endDate,
                reason = reason
            )
            supabaseClient.postgrest["leave_requests"].insert(dto)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit leave request")
        }
    }

    override suspend fun getMyLeaveRequests(): Flow<Resource<List<LeaveRequest>>> = flow {
        emit(Resource.Loading())
        try {
            val user = supabaseClient.auth.currentUserOrNull()
                ?: return@flow emit(Resource.Error("Not authenticated"))

            val dtos = supabaseClient.postgrest["leave_requests"]
                .select {
                    filter { eq("employee_id", user.id) }
                }
                .decodeList<LeaveRequestDto>()

            emit(Resource.Success(dtos.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch leave requests"))
        }
    }

    override suspend fun getTeamLeaveRequests(): Flow<Resource<List<LeaveRequest>>> = flow {
        emit(Resource.Loading())
        try {
            // For TL: fetch all leave requests with PENDING status
            // In a full implementation, this would filter by team_id
            val dtos = supabaseClient.postgrest["leave_requests"]
                .select {
                    filter { eq("status", "PENDING") }
                }
                .decodeList<LeaveRequestDto>()

            emit(Resource.Success(dtos.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch team leave requests"))
        }
    }

    override suspend fun getAllLeaveRequests(): Flow<Resource<List<LeaveRequest>>> = flow {
        emit(Resource.Loading())
        try {
            val dtos = supabaseClient.postgrest["leave_requests"]
                .select()
                .decodeList<LeaveRequestDto>()

            emit(Resource.Success(dtos.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch all leave requests"))
        }
    }

    override suspend fun approveLeaveRequest(
        id: String,
        newStatus: String,
        comment: String
    ): Resource<Unit> {
        return try {
            val isLeaderAction = newStatus == "APPROVED_BY_LEADER" || newStatus == "REJECTED"
            val commentField = if (isLeaderAction) "leader_comment" else "hr_comment"

            val updateData = mapOf(
                "status" to newStatus,
                commentField to comment
            )

            supabaseClient.postgrest["leave_requests"].update(updateData) {
                filter { eq("id", id) }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update leave request")
        }
    }
}

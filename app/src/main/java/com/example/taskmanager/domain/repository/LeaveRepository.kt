package com.example.taskmanager.domain.repository

import com.example.taskmanager.domain.model.LeaveRequest
import com.example.taskmanager.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface LeaveRepository {
    suspend fun submitLeaveRequest(
        leaveType: String,
        startDate: String,
        endDate: String,
        reason: String
    ): Resource<Unit>

    suspend fun getMyLeaveRequests(): Flow<Resource<List<LeaveRequest>>>

    suspend fun getTeamLeaveRequests(): Flow<Resource<List<LeaveRequest>>>

    suspend fun getAllLeaveRequests(): Flow<Resource<List<LeaveRequest>>>

    suspend fun approveLeaveRequest(
        id: String,
        newStatus: String,
        comment: String
    ): Resource<Unit>
}

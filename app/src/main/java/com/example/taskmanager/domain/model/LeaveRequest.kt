package com.example.taskmanager.domain.model

data class LeaveRequest(
    val id: String,
    val employeeId: String?,
    val leaveType: String,
    val startDate: String,
    val endDate: String,
    val reason: String?,
    val status: LeaveStatus,
    val leaderComment: String?,
    val hrComment: String?,
    val createdAt: String?
)

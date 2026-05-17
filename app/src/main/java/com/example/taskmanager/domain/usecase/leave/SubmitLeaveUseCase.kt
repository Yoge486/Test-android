package com.example.taskmanager.domain.usecase.leave

import com.example.taskmanager.domain.repository.LeaveRepository
import javax.inject.Inject

class SubmitLeaveUseCase @Inject constructor(
    private val leaveRepository: LeaveRepository
) {
    suspend operator fun invoke(
        leaveType: String,
        startDate: String,
        endDate: String,
        reason: String
    ) = leaveRepository.submitLeaveRequest(leaveType, startDate, endDate, reason)
}

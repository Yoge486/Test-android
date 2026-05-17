package com.example.taskmanager.domain.usecase.leave

import com.example.taskmanager.domain.repository.LeaveRepository
import javax.inject.Inject

class ApproveLeaveUseCase @Inject constructor(
    private val leaveRepository: LeaveRepository
) {
    suspend operator fun invoke(id: String, newStatus: String, comment: String) =
        leaveRepository.approveLeaveRequest(id, newStatus, comment)
}

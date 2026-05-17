package com.example.taskmanager.domain.usecase.leave

import com.example.taskmanager.domain.repository.LeaveRepository
import javax.inject.Inject

class GetTeamLeavesUseCase @Inject constructor(
    private val leaveRepository: LeaveRepository
) {
    suspend operator fun invoke() = leaveRepository.getTeamLeaveRequests()
}

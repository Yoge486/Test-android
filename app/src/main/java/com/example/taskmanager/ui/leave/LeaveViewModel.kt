package com.example.taskmanager.ui.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.LeaveRequest
import com.example.taskmanager.domain.model.UserRole
import com.example.taskmanager.domain.usecase.leave.ApproveLeaveUseCase
import com.example.taskmanager.domain.usecase.leave.GetMyLeavesUseCase
import com.example.taskmanager.domain.usecase.leave.GetTeamLeavesUseCase
import com.example.taskmanager.domain.usecase.leave.SubmitLeaveUseCase
import com.example.taskmanager.domain.usecase.profile.GetProfileUseCase
import com.example.taskmanager.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaveState(
    val myLeaves: List<LeaveRequest> = emptyList(),
    val teamLeaves: List<LeaveRequest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val submitResult: Resource<Unit>? = null,
    val approveResult: Resource<Unit>? = null,
    val userRole: UserRole = UserRole.EMPLOYEE
)

@HiltViewModel
class LeaveViewModel @Inject constructor(
    private val submitLeaveUseCase: SubmitLeaveUseCase,
    private val getMyLeavesUseCase: GetMyLeavesUseCase,
    private val getTeamLeavesUseCase: GetTeamLeavesUseCase,
    private val approveLeaveUseCase: ApproveLeaveUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LeaveState())
    val state: StateFlow<LeaveState> = _state.asStateFlow()

    init {
        loadProfile()
        loadMyLeaves()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getProfileUseCase().collect { result ->
                if (result is Resource.Success) {
                    val role = result.data?.role ?: UserRole.EMPLOYEE
                    _state.value = _state.value.copy(userRole = role)
                    if (role != UserRole.EMPLOYEE) {
                        loadTeamLeaves()
                    }
                }
            }
        }
    }

    fun loadMyLeaves() {
        viewModelScope.launch {
            getMyLeavesUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> _state.value = _state.value.copy(
                        myLeaves = result.data ?: emptyList(),
                        isLoading = false
                    )
                    is Resource.Error -> _state.value = _state.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun loadTeamLeaves() {
        viewModelScope.launch {
            getTeamLeavesUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> _state.value = _state.value.copy(
                        teamLeaves = result.data ?: emptyList()
                    )
                    is Resource.Error -> {}
                }
            }
        }
    }

    fun submitLeave(leaveType: String, startDate: String, endDate: String, reason: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(submitResult = Resource.Loading())
            val result = submitLeaveUseCase(leaveType, startDate, endDate, reason)
            _state.value = _state.value.copy(submitResult = result)
            if (result is Resource.Success) {
                loadMyLeaves()
            }
        }
    }

    fun approveLeave(id: String, newStatus: String, comment: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(approveResult = Resource.Loading())
            val result = approveLeaveUseCase(id, newStatus, comment)
            _state.value = _state.value.copy(approveResult = result)
            if (result is Resource.Success) {
                loadTeamLeaves()
            }
        }
    }

    fun clearSubmitResult() {
        _state.value = _state.value.copy(submitResult = null)
    }
}

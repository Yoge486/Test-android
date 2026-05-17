package com.example.taskmanager.ui.home.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.PasswordResetRequest
import com.example.taskmanager.domain.repository.AuthRepository
import com.example.taskmanager.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PendingResetsState(
    val requests: List<PasswordResetRequest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PendingResetsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PendingResetsState())
    val state: StateFlow<PendingResetsState> = _state.asStateFlow()

    init {
        loadPendingResets()
    }

    private fun loadPendingResets() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = authRepository.getPendingResets()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, requests = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                else -> Unit
            }
        }
    }

    fun approveRequest(id: String) {
        updateStatus(id, "APPROVED")
    }

    fun rejectRequest(id: String) {
        updateStatus(id, "REJECTED")
    }

    private fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            when (authRepository.updateResetRequestStatus(id, status)) {
                is Resource.Success -> {
                    loadPendingResets() // Refresh list
                }
                is Resource.Error -> {
                    // Handle error silently or post to a one-time event
                }
                else -> Unit
            }
        }
    }
}

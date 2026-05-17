package com.example.taskmanager.ui.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.repository.AuthRepository
import com.example.taskmanager.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null,
    val status: String? = null // PENDING, APPROVED, REJECTED
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    fun submitRequest(email: String, reason: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = authRepository.submitPasswordResetRequest(email, reason)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, isSubmitted = true)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                else -> Unit
            }
        }
    }

    fun checkStatus(email: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = authRepository.checkPasswordResetStatus(email)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, status = result.data)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                else -> Unit
            }
        }
    }
}

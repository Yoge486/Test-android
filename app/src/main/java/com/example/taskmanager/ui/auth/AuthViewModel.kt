package com.example.taskmanager.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.UserRole
import com.example.taskmanager.domain.usecase.auth.LoginUseCase
import com.example.taskmanager.domain.usecase.auth.RegisterUseCase
import com.example.taskmanager.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null, success = false) }
            val result = loginUseCase(email, password)
            handleResult(result)
        }
    }

    fun register(email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null, success = false) }
            val result = registerUseCase(email, password, role) 
            handleResult(result)
        }
    }

    private fun handleResult(result: Resource<Unit>) {
        when (result) {
            is Resource.Success -> {
                _authState.update { it.copy(isLoading = false, success = true, error = null) }
            }
            is Resource.Error -> {
                _authState.update { it.copy(isLoading = false, error = result.message ?: "An unknown error occurred") }
            }
            is Resource.Loading -> {
                _authState.update { it.copy(isLoading = true, error = null) }
            }
        }
    }

    fun resetState() {
        _authState.update { AuthState() }
    }
}

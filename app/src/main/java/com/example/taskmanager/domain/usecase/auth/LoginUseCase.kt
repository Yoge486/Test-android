package com.example.taskmanager.domain.usecase.auth

import com.example.taskmanager.domain.repository.AuthRepository
import com.example.taskmanager.domain.util.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<Unit> {
        if(email.isBlank() || password.isBlank()) {
            return Resource.Error("Email and password cannot be empty")
        }
        return repository.login(email, password)
    }
}

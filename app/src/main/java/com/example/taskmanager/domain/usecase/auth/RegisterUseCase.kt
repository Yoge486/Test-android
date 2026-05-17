package com.example.taskmanager.domain.usecase.auth

import com.example.taskmanager.domain.model.UserRole
import com.example.taskmanager.domain.repository.AuthRepository
import com.example.taskmanager.domain.util.Resource
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, role: UserRole): Resource<Unit> {
        if(email.isBlank() || password.isBlank()) {
            return Resource.Error("Email and password cannot be empty")
        }
        if(password.length < 6) {
            return Resource.Error("Password must be at least 6 characters")
        }
        return repository.register(email, password, role)
    }
}

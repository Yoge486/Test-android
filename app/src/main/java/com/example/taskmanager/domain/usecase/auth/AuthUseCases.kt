package com.example.taskmanager.domain.usecase.auth

data class AuthUseCases(
    val loginUseCase: LoginUseCase,
    val registerUseCase: RegisterUseCase
)

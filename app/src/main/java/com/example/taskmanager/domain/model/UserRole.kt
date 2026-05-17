package com.example.taskmanager.domain.model

enum class UserRole(val displayName: String) {
    EMPLOYEE("Employee"),
    TEAM_LEADER("Team Leader"),
    HR("HR"),
    HIGHER_OFFICIAL("Higher Official")
}

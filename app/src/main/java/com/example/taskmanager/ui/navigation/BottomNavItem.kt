package com.example.taskmanager.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "dashboard",
        title = "Home",
        icon = Icons.Filled.Home
    )
    object Tasks : BottomNavItem(
        route = "tasks",
        title = "Tasks",
        icon = Icons.AutoMirrored.Filled.List
    )
    object Community : BottomNavItem(
        route = "community",
        title = "Community",
        icon = Icons.AutoMirrored.Filled.Send
    )
    object Schedule : BottomNavItem(
        route = "schedule",
        title = "Schedule",
        icon = Icons.Filled.DateRange
    )
    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        icon = Icons.Filled.Person
    )
}

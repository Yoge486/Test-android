package com.example.taskmanager.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanager.ui.home.dashboards.EmployeeDashboardScreen
import com.example.taskmanager.ui.home.dashboards.ExecutiveDashboardScreen
import com.example.taskmanager.ui.home.dashboards.HrDashboardScreen
import com.example.taskmanager.ui.home.dashboards.TlDashboardScreen
import com.example.taskmanager.ui.profile.ProfileViewModel

@Composable
fun HomeScreen(
    onNavigateToPendingResets: () -> Unit = {},
    onNavigateToLeave: () -> Unit = {},
    onNavigateToCreateTask: () -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by profileViewModel.state.collectAsState()

    when {
        profileState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        profileState.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${profileState.error}", color = MaterialTheme.colorScheme.error)
            }
        }
        profileState.profile != null -> {
            val role = profileState.profile!!.role.name
            when (role) {
                "EMPLOYEE" -> EmployeeDashboardScreen(
                    onNavigateToLeave = onNavigateToLeave,
                    onNavigateToTasks = onNavigateToTasks
                )
                "TEAM_LEADER" -> TlDashboardScreen(
                    onNavigateToLeave = onNavigateToLeave,
                    onNavigateToCreateTask = onNavigateToCreateTask,
                    onNavigateToTasks = onNavigateToTasks,
                    onNavigateToCommunity = onNavigateToCommunity
                )
                "HR" -> HrDashboardScreen(
                    onNavigateToLeave = onNavigateToLeave,
                    onNavigateToPendingResets = onNavigateToPendingResets,
                    onNavigateToTasks = onNavigateToTasks,
                    onNavigateToCommunity = onNavigateToCommunity
                )
                "HIGHER_OFFICIAL" -> ExecutiveDashboardScreen(
                    onNavigateToLeave = onNavigateToLeave,
                    onNavigateToPendingResets = onNavigateToPendingResets
                )
                else -> EmployeeDashboardScreen(
                    onNavigateToLeave = onNavigateToLeave,
                    onNavigateToTasks = onNavigateToTasks
                )
            }
        }
    }
}

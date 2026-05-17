package com.example.taskmanager.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskmanager.ui.community.CommunityScreen
import com.example.taskmanager.ui.home.HomeScreen
import com.example.taskmanager.ui.leave.LeaveScreen
import com.example.taskmanager.ui.navigation.BottomNavItem
import com.example.taskmanager.ui.profile.ProfileScreen
import com.example.taskmanager.ui.schedule.ScheduleScreen
import com.example.taskmanager.ui.tasks.TasksScreen
import com.example.taskmanager.ui.tasks.create.CreateTaskScreen
import com.example.taskmanager.ui.tasks.detail.TaskDetailScreen

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onNavigateToPendingResets: () -> Unit
) {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Tasks,
        BottomNavItem.Community,
        BottomNavItem.Schedule,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        onNavigateToPendingResets = onNavigateToPendingResets,
                        onNavigateToLeave = { navController.navigate("leave") },
                        onNavigateToCreateTask = { navController.navigate("create_task") },
                        onNavigateToTasks = {
                            navController.navigate(BottomNavItem.Tasks.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToCommunity = {
                            navController.navigate(BottomNavItem.Community.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(BottomNavItem.Tasks.route) {
                    TasksScreen(
                        onCreateTaskClick = { navController.navigate("create_task") },
                        onTaskClick = { taskId -> navController.navigate("task_detail/$taskId") }
                    )
                }
                composable("create_task") {
                    CreateTaskScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "task_detail/{taskId}",
                    arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                ) {
                    TaskDetailScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("leave") {
                    LeaveScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(BottomNavItem.Community.route) { CommunityScreen() }
                composable(BottomNavItem.Schedule.route) { ScheduleScreen() }
                composable(BottomNavItem.Profile.route) { ProfileScreen(onLogout = onLogout) }
            }
        }
    }
}

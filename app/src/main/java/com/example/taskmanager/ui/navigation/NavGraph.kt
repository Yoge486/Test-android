package com.example.taskmanager.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taskmanager.ui.auth.LoginScreen
import com.example.taskmanager.ui.auth.RegisterScreen
import com.example.taskmanager.ui.auth.forgot.ForgotPasswordScreen
import com.example.taskmanager.ui.auth.reset.ResetPasswordScreen
import com.example.taskmanager.ui.home.admin.PendingResetsScreen

import com.example.taskmanager.ui.main.MainScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password/{email}"
    const val PENDING_RESETS = "pending_resets"
}

@Composable
fun TaskManagerNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }
        
        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Routes.HOME) {
            MainScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToPendingResets = {
                    navController.navigate(Routes.PENDING_RESETS)
                }
            )
        }
        
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToReset = { email ->
                    navController.navigate("reset_password/$email") {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.RESET_PASSWORD) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(
                email = email,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) // clear stack
                    }
                }
            )
        }

        composable(Routes.PENDING_RESETS) {
            PendingResetsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

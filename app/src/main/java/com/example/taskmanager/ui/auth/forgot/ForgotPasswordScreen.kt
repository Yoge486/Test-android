package com.example.taskmanager.ui.auth.forgot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReset: (String) -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    
    // Auto-navigate if approved
    LaunchedEffect(state.status) {
        if (state.status == "APPROVED") {
            onNavigateToReset(email)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Reset Password", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isSubmitted) {
            Text("Request submitted! Waiting for Admin approval.", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.checkStatus(email) }, enabled = !state.isLoading) {
                Text("Check Status")
            }
            if (state.status == "REJECTED") {
                Text("Your request was rejected by the admin.", color = MaterialTheme.colorScheme.error)
            }
        } else {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason for reset") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.submitRequest(email, reason) },
                enabled = email.isNotBlank() && reason.isNotBlank() && !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Request")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateBack) {
            Text("Back to Login")
        }
        
        if (state.error != null) {
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }
    }
}

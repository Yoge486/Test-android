package com.example.taskmanager.ui.tasks.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanager.domain.model.TaskPriority
import com.example.taskmanager.domain.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: CreateTaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val dueDate by viewModel.dueDate.collectAsState()
    val createTaskState by viewModel.createTaskState.collectAsState()

    var expandedPriority by remember { mutableStateOf(false) }

    // Handle side effects for navigation
    LaunchedEffect(createTaskState) {
        if (createTaskState is Resource.Success) {
            viewModel.resetState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (createTaskState is Resource.Error) {
                Text(
                    text = (createTaskState as Resource.Error).message ?: "Error creating task",
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.setTitle(it) },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.setDescription(it) },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = priority,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Priority") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expandedPriority = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Priority")
                        }
                    }
                )
                Box(modifier = Modifier.matchParentSize().clickable { expandedPriority = true })
                DropdownMenu(
                    expanded = expandedPriority,
                    onDismissRequest = { expandedPriority = false }
                ) {
                    TaskPriority.values().forEach { prio ->
                        DropdownMenuItem(
                            text = { Text(prio.name) },
                            onClick = {
                                viewModel.setPriority(prio.name)
                                expandedPriority = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = dueDate,
                onValueChange = { viewModel.setDueDate(it) },
                label = { Text("Due Date (e.g., 2026-05-10) Optional") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { viewModel.createTask() },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = createTaskState !is Resource.Loading && title.isNotBlank()
            ) {
                if (createTaskState is Resource.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Create Task")
                }
            }
        }
    }
}

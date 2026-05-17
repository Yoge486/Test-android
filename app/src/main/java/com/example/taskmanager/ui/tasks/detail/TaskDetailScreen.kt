package com.example.taskmanager.ui.tasks.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var expandedStatus by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.error ?: "Error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                state.task != null -> {
                    val task = state.task!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        // Status & Priority Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Priority chip
                            val priorityColor = when (task.priority.name) {
                                "HIGH" -> PriorityHigh
                                "MEDIUM" -> PriorityMedium
                                else -> PriorityLow
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = priorityColor.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(8.dp).clip(CircleShape)
                                            .background(priorityColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = task.priority.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = priorityColor
                                    )
                                }
                            }

                            // Status chip
                            val statusColor = when (task.status) {
                                TaskStatus.COMPLETED -> StatusCompleted
                                TaskStatus.IN_PROGRESS -> StatusInProgress
                                TaskStatus.OVERDUE -> StatusOverdue
                                TaskStatus.UNDER_REVIEW -> StatusUnderReview
                                TaskStatus.ESCALATED -> StatusEscalated
                                else -> StatusToDo
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = statusColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = task.status.name.replace("_", " "),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = statusColor
                                )
                            }
                        }

                        HorizontalDivider()

                        // Description
                        if (!task.description.isNullOrBlank()) {
                            Column {
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = task.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Details Card
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (!task.dueDate.isNullOrBlank()) {
                                    DetailRow(
                                        icon = Icons.Default.CalendarToday,
                                        label = "Due Date",
                                        value = task.dueDate
                                    )
                                }
                                if (!task.category.isNullOrBlank()) {
                                    DetailRow(
                                        icon = Icons.Default.Label,
                                        label = "Category",
                                        value = task.category
                                    )
                                }
                                if (!task.assigneeId.isNullOrBlank()) {
                                    DetailRow(
                                        icon = Icons.Default.Person,
                                        label = "Assignee ID",
                                        value = task.assigneeId.take(12) + "…"
                                    )
                                }
                                if (!task.creatorId.isNullOrBlank()) {
                                    DetailRow(
                                        icon = Icons.Default.PersonOutline,
                                        label = "Creator ID",
                                        value = task.creatorId.take(12) + "…"
                                    )
                                }
                                if (!task.createdAt.isNullOrBlank()) {
                                    DetailRow(
                                        icon = Icons.Default.AccessTime,
                                        label = "Created At",
                                        value = task.createdAt.take(16).replace("T", " ")
                                    )
                                }
                            }
                        }

                        // Update Status Section
                        Text(
                            text = "Update Status",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Box {
                            Button(
                                onClick = { expandedStatus = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Change Status: ${task.status.name.replace("_", " ")}")
                            }
                            DropdownMenu(
                                expanded = expandedStatus,
                                onDismissRequest = { expandedStatus = false }
                            ) {
                                TaskStatus.values().forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status.name.replace("_", " ")) },
                                        onClick = {
                                            viewModel.updateStatus(status)
                                            expandedStatus = false
                                        },
                                        enabled = status != task.status
                                    )
                                }
                            }
                        }

                        // Update result feedback
                        val updateResult = state.updateResult
                        if (updateResult is com.example.taskmanager.domain.util.Resource.Success) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = StatusCompleted.copy(alpha = 0.12f)
                                )
                            ) {
                                Text(
                                    text = "✓ Status updated successfully",
                                    modifier = Modifier.padding(12.dp),
                                    color = StatusCompleted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else if (updateResult is com.example.taskmanager.domain.util.Resource.Error) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = updateResult.message ?: "Error updating status",
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

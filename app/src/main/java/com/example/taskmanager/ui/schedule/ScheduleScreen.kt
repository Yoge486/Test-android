package com.example.taskmanager.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanager.data.local.PersonalTodo
import com.example.taskmanager.domain.util.Resource
import com.example.taskmanager.ui.tasks.TaskCard
import com.example.taskmanager.ui.tasks.TasksViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    scheduleViewModel: ScheduleViewModel = hiltViewModel(),
    tasksViewModel: TasksViewModel = hiltViewModel()
) {
    val selectedDate by scheduleViewModel.selectedDate.collectAsState()
    val todos by scheduleViewModel.todos.collectAsState()
    val tasksState by tasksViewModel.tasksState.collectAsState()

    var showAddTodoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTodoDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            CalendarStrip(
                selectedDate = selectedDate,
                onDateSelected = { scheduleViewModel.selectDate(it) }
            )

            HorizontalDivider()

            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                item {
                    Text("Personal To-Dos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (todos.isEmpty()) {
                    item { Text("No to-dos scheduled for this date.", color = Color.Gray) }
                } else {
                    items(todos) { todo ->
                        TodoItemCard(
                            todo = todo,
                            onToggle = { scheduleViewModel.toggleTodo(todo) },
                            onDelete = { scheduleViewModel.deleteTodo(todo) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Company Tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                when (val state = tasksState) {
                    is Resource.Loading -> {
                        item { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) }
                    }
                    is Resource.Error -> {
                        item { Text(state.message ?: "Error loads tasks", color = MaterialTheme.colorScheme.error) }
                    }
                    is Resource.Success -> {
                        // Filter tasks where due date starts with our selected date natively
                        val dateStr = selectedDate.toString()
                        val dailyTasks = state.data?.filter { it.dueDate?.startsWith(dateStr) == true } ?: emptyList()
                        
                        if (dailyTasks.isEmpty()) {
                            item { Text("No company tasks due on this date.", color = Color.Gray) }
                        } else {
                            items(dailyTasks) { task ->
                                TaskCard(
                                    task = task,
                                    onStatusChange = { newStatus -> tasksViewModel.updateTaskStatus(task.id, newStatus) }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }

        if (showAddTodoDialog) {
            var todoText by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddTodoDialog = false },
                title = { Text("Add Personal To-Do") },
                text = {
                    OutlinedTextField(
                        value = todoText,
                        onValueChange = { todoText = it },
                        label = { Text("Todo Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = { 
                        scheduleViewModel.addTodo(todoText)
                        showAddTodoDialog = false
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTodoDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun CalendarStrip(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Show 7 days: 3 days before, today, 3 days after
    val dates = (-3..3).map { LocalDate.now().plusDays(it.toLong()) }
    
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(dates) { date ->
            val isSelected = date == selectedDate
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDateSelected(date) }
                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                    .padding(8.dp)
            ) {
                Text(
                    text = date.dayOfWeek.name.take(3), 
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun TodoItemCard(
    todo: PersonalTodo,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = todo.title,
                modifier = Modifier.weight(1f),
                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (todo.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Todo", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

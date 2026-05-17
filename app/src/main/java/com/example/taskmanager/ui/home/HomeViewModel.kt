package com.example.taskmanager.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.domain.usecase.task.GetTasksUseCase
import com.example.taskmanager.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardStats(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val overdueTasks: Int = 0,
    val todoTasks: Int = 0,
    val underReviewTasks: Int = 0,
    val completionRate: Float = 0f,
    val upcomingTasks: List<Task> = emptyList()
)

data class DashboardState(
    val stats: DashboardStats = DashboardStats(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            getTasksUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        val tasks = result.data ?: emptyList()
                        val stats = computeStats(tasks)
                        _state.value = _state.value.copy(
                            stats = stats,
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            error = result.message ?: "Failed to load dashboard",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun computeStats(tasks: List<Task>): DashboardStats {
        val total = tasks.size
        val completed = tasks.count { it.status == TaskStatus.COMPLETED }
        val inProgress = tasks.count { it.status == TaskStatus.IN_PROGRESS }
        val overdue = tasks.count { it.status == TaskStatus.OVERDUE }
        val todo = tasks.count { it.status == TaskStatus.TO_DO }
        val underReview = tasks.count { it.status == TaskStatus.UNDER_REVIEW }
        val rate = if (total > 0) completed.toFloat() / total else 0f

        // Upcoming: non-completed tasks sorted by due date, take 5
        val upcoming = tasks
            .filter { it.status != TaskStatus.COMPLETED }
            .sortedBy { it.dueDate ?: "9999-12-31" }
            .take(5)

        return DashboardStats(
            totalTasks = total,
            completedTasks = completed,
            inProgressTasks = inProgress,
            overdueTasks = overdue,
            todoTasks = todo,
            underReviewTasks = underReview,
            completionRate = rate,
            upcomingTasks = upcoming
        )
    }
}

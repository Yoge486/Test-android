package com.example.taskmanager.ui.tasks.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.domain.usecase.task.GetTasksUseCase
import com.example.taskmanager.domain.usecase.task.UpdateTaskStatusUseCase
import com.example.taskmanager.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val updateResult: Resource<Unit>? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModel() {

    private val taskId: String = savedStateHandle["taskId"] ?: ""

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            getTasksUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val task = result.data?.find { it.id == taskId }
                        _state.value = _state.value.copy(
                            task = task,
                            isLoading = false,
                            error = if (task == null) "Task not found" else null
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            error = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun updateStatus(newStatus: TaskStatus) {
        viewModelScope.launch {
            _state.value = _state.value.copy(updateResult = Resource.Loading())
            val result = updateTaskStatusUseCase(taskId, newStatus)
            _state.value = _state.value.copy(updateResult = result)
            if (result is Resource.Success) {
                // Reload the task to reflect the update
                loadTask()
            }
        }
    }
}

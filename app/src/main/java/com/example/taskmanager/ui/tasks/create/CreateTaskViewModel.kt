package com.example.taskmanager.ui.tasks.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Profile
import com.example.taskmanager.domain.usecase.profile.GetProfileUseCase
import com.example.taskmanager.domain.usecase.task.CreateTaskUseCase
import com.example.taskmanager.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.taskmanager.domain.repository.ProfileRepository

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _priority = MutableStateFlow("MEDIUM")
    val priority: StateFlow<String> = _priority.asStateFlow()

    private val _dueDate = MutableStateFlow("")
    val dueDate: StateFlow<String> = _dueDate.asStateFlow()

    private val _selectedAssignee = MutableStateFlow<Profile?>(null)
    val selectedAssignee: StateFlow<Profile?> = _selectedAssignee.asStateFlow()

    private val _teamMembers = MutableStateFlow<List<Profile>>(emptyList())
    val teamMembers: StateFlow<List<Profile>> = _teamMembers.asStateFlow()

    private val _createTaskState = MutableStateFlow<Resource<Unit>?>(null)
    val createTaskState: StateFlow<Resource<Unit>?> = _createTaskState.asStateFlow()

    init {
        loadTeamMembers()
    }

    private fun loadTeamMembers() {
        viewModelScope.launch {
            profileRepository.getTeamMembers().collect { result ->
                if (result is Resource.Success) {
                    _teamMembers.value = result.data ?: emptyList()
                }
            }
        }
    }

    fun setTitle(value: String) { _title.value = value }
    fun setDescription(value: String) { _description.value = value }
    fun setPriority(value: String) { _priority.value = value }
    fun setDueDate(value: String) { _dueDate.value = value }
    fun setAssignee(profile: Profile?) { _selectedAssignee.value = profile }

    fun createTask() {
        viewModelScope.launch {
            _createTaskState.value = Resource.Loading()
            val result = createTaskUseCase(
                title = title.value,
                description = description.value.ifBlank { null },
                priority = priority.value,
                dueDate = dueDate.value.ifBlank { null },
                assigneeId = _selectedAssignee.value?.id
            )
            _createTaskState.value = result
        }
    }

    fun resetState() {
        _createTaskState.value = null
    }
}

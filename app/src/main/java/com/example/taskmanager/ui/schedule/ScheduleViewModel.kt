package com.example.taskmanager.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.local.PersonalTodo
import com.example.taskmanager.data.local.TodoDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val todoDao: TodoDao
) : ViewModel() {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _todos = MutableStateFlow<List<PersonalTodo>>(emptyList())
    val todos: StateFlow<List<PersonalTodo>> = _todos.asStateFlow()

    init {
        loadTodosForDate(_selectedDate.value)
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadTodosForDate(date)
    }

    private fun loadTodosForDate(date: LocalDate) {
        viewModelScope.launch {
            val dateStr = date.format(formatter)
            todoDao.getTodosForDate(dateStr).collect {
                _todos.value = it
            }
        }
    }

    fun addTodo(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(formatter)
            todoDao.insertTodo(
                PersonalTodo(title = title, dateString = dateStr)
            )
        }
    }

    fun toggleTodo(todo: PersonalTodo) {
        viewModelScope.launch {
            todoDao.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todo: PersonalTodo) {
        viewModelScope.launch {
            todoDao.deleteTodo(todo)
        }
    }
}

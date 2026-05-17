package com.example.taskmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_todos")
data class PersonalTodo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val dateString: String // Format: YYYY-MM-DD
)

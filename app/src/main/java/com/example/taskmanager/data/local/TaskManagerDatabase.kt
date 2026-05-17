package com.example.taskmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PersonalTodo::class], version = 1, exportSchema = false)
abstract class TaskManagerDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao
}

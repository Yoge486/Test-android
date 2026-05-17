package com.example.taskmanager.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM personal_todos WHERE dateString = :date ORDER BY id DESC")
    fun getTodosForDate(date: String): Flow<List<PersonalTodo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: PersonalTodo)

    @Update
    suspend fun updateTodo(todo: PersonalTodo)

    @Delete
    suspend fun deleteTodo(todo: PersonalTodo)
}

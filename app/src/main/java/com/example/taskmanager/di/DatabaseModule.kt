package com.example.taskmanager.di

import android.app.Application
import androidx.room.Room
import com.example.taskmanager.data.local.TaskManagerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTaskManagerDatabase(app: Application): TaskManagerDatabase {
        return Room.databaseBuilder(
            app,
            TaskManagerDatabase::class.java,
            "taskmanager_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTodoDao(db: TaskManagerDatabase) = db.todoDao
}

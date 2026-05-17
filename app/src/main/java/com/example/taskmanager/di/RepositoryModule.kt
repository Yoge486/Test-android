package com.example.taskmanager.di

import com.example.taskmanager.data.repository.AuthRepositoryImpl
import com.example.taskmanager.data.repository.ProfileRepositoryImpl
import com.example.taskmanager.data.repository.TaskRepositoryImpl
import com.example.taskmanager.data.repository.CommunityRepositoryImpl
import com.example.taskmanager.data.repository.LeaveRepositoryImpl
import com.example.taskmanager.domain.repository.AuthRepository
import com.example.taskmanager.domain.repository.ProfileRepository
import com.example.taskmanager.domain.repository.TaskRepository
import com.example.taskmanager.domain.repository.CommunityRepository
import com.example.taskmanager.domain.repository.LeaveRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(
        communityRepositoryImpl: CommunityRepositoryImpl
    ): CommunityRepository

    @Binds
    @Singleton
    abstract fun bindLeaveRepository(
        leaveRepositoryImpl: LeaveRepositoryImpl
    ): LeaveRepository

}


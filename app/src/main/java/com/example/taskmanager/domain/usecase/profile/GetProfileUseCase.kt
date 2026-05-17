package com.example.taskmanager.domain.usecase.profile

import com.example.taskmanager.domain.model.Profile
import com.example.taskmanager.domain.repository.ProfileRepository
import com.example.taskmanager.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): Flow<Resource<Profile>> {
        return profileRepository.getProfile()
    }
}

package com.example.taskmanager.domain.usecase.profile

import com.example.taskmanager.domain.repository.ProfileRepository
import com.example.taskmanager.domain.util.Resource
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(fullName: String, department: String): Resource<Unit> {
        if (fullName.isBlank()) {
            return Resource.Error("Name cannot be blank")
        }
        return profileRepository.updateProfile(fullName, department)
    }
}

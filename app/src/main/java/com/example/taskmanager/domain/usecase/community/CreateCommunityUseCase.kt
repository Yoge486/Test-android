package com.example.taskmanager.domain.usecase.community

import com.example.taskmanager.domain.repository.CommunityRepository
import com.example.taskmanager.domain.util.Resource
import javax.inject.Inject

class CreateCommunityUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String?,
        isOrgWide: Boolean
    ): Resource<Unit> {
        if (name.isBlank()) {
            return Resource.Error("Community name cannot be blank")
        }
        return repository.createCommunity(name, description, isOrgWide)
    }
}

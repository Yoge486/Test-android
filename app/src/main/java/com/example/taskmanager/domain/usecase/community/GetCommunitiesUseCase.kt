package com.example.taskmanager.domain.usecase.community

import com.example.taskmanager.domain.model.Community
import com.example.taskmanager.domain.repository.CommunityRepository
import com.example.taskmanager.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommunitiesUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<Community>>> {
        return repository.getCommunities()
    }
}

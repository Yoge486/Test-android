package com.example.taskmanager.domain.usecase.community

import com.example.taskmanager.domain.model.Message
import com.example.taskmanager.domain.repository.CommunityRepository
import com.example.taskmanager.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(communityId: String): Flow<Resource<List<Message>>> {
        return repository.getMessages(communityId)
    }
}

package com.example.taskmanager.domain.usecase.community

import com.example.taskmanager.domain.repository.CommunityRepository
import com.example.taskmanager.domain.util.Resource
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(communityId: String, content: String): Resource<Unit> {
        return repository.sendMessage(communityId, content)
    }
}

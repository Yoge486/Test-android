package com.example.taskmanager.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Community
import com.example.taskmanager.domain.model.Message
import com.example.taskmanager.domain.usecase.community.CreateCommunityUseCase
import com.example.taskmanager.domain.usecase.community.GetCommunitiesUseCase
import com.example.taskmanager.domain.usecase.community.GetMessagesUseCase
import com.example.taskmanager.domain.usecase.community.SendMessageUseCase
import com.example.taskmanager.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommunityListState(
    val communities: List<Community> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSending: Boolean = false,
    val sendError: String? = null
)

data class CreateCommunityState(
    val isCreating: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val getCommunitiesUseCase: GetCommunitiesUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val createCommunityUseCase: CreateCommunityUseCase
) : ViewModel() {

    private val _communityListState = MutableStateFlow(CommunityListState())
    val communityListState: StateFlow<CommunityListState> = _communityListState.asStateFlow()

    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    private val _selectedCommunity = MutableStateFlow<Community?>(null)
    val selectedCommunity: StateFlow<Community?> = _selectedCommunity.asStateFlow()

    private val _createCommunityState = MutableStateFlow(CreateCommunityState())
    val createCommunityState: StateFlow<CreateCommunityState> = _createCommunityState.asStateFlow()

    init {
        loadCommunities()
    }

    fun loadCommunities() {
        viewModelScope.launch {
            getCommunitiesUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _communityListState.value = _communityListState.value.copy(
                            isLoading = true, error = null
                        )
                    }
                    is Resource.Success -> {
                        _communityListState.value = _communityListState.value.copy(
                            communities = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        _communityListState.value = _communityListState.value.copy(
                            error = result.message ?: "Failed to load communities",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun selectCommunity(community: Community) {
        _selectedCommunity.value = community
        loadMessages(community.id)
    }

    fun clearSelectedCommunity() {
        _selectedCommunity.value = null
        _chatState.value = ChatState()
    }

    fun loadMessages(communityId: String) {
        viewModelScope.launch {
            getMessagesUseCase(communityId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _chatState.value = _chatState.value.copy(
                            isLoading = true, error = null
                        )
                    }
                    is Resource.Success -> {
                        _chatState.value = _chatState.value.copy(
                            messages = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        _chatState.value = _chatState.value.copy(
                            error = result.message ?: "Failed to load messages",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun sendMessage(content: String) {
        val community = _selectedCommunity.value ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            _chatState.value = _chatState.value.copy(isSending = true, sendError = null)
            when (val result = sendMessageUseCase(community.id, content)) {
                is Resource.Success -> {
                    _chatState.value = _chatState.value.copy(isSending = false)
                    // Reload messages to show the new one
                    loadMessages(community.id)
                }
                is Resource.Error -> {
                    _chatState.value = _chatState.value.copy(
                        isSending = false,
                        sendError = result.message ?: "Failed to send message"
                    )
                }
                else -> Unit
            }
        }
    }

    fun createCommunity(name: String, description: String?, isOrgWide: Boolean) {
        viewModelScope.launch {
            _createCommunityState.value = CreateCommunityState(isCreating = true)
            when (val result = createCommunityUseCase(name, description, isOrgWide)) {
                is Resource.Success -> {
                    _createCommunityState.value = CreateCommunityState(success = true)
                    loadCommunities() // Refresh the list
                }
                is Resource.Error -> {
                    _createCommunityState.value = CreateCommunityState(
                        error = result.message ?: "Failed to create community"
                    )
                }
                else -> Unit
            }
        }
    }

    fun resetCreateState() {
        _createCommunityState.value = CreateCommunityState()
    }
}

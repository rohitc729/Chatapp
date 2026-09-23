package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitchauhan.hiichat.data.remote.firebase.FirebaseService
import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import com.rohitchauhan.hiichat.domain.use_case.GetCurrentUserUC
import com.rohitchauhan.hiichat.domain.use_case.GetUserByIdUC
import com.rohitchauhan.hiichat.domain.use_case.GetUserChatsUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListScreenVM @Inject constructor(
    private val getUserChatsUC: GetUserChatsUC,
    private val getUserByIdUC: GetUserByIdUC,
    private val getCurrentUserUC: GetCurrentUserUC,
    private val firebaseService: FirebaseService
) : ViewModel() {

    private val _chatListState = MutableStateFlow<List<ChatUiModel>>(emptyList())
    val chatListState = _chatListState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserDto?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        getChatList()
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUC().collect { user ->
                _currentUser.value = user
            }
        }
    }

    private fun getChatList() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUid = firebaseService.getCurrentUid()
            getUserChatsUC()
                .flatMapLatest { chats ->
                    if (chats.isEmpty()) {
                        flowOf(emptyList<ChatUiModel>())
                    } else {
                        val flowList = chats.map { chat ->
                            val otherUserId = chat.members.firstOrNull { it != currentUid } ?: ""
                            getUserByIdUC(otherUserId).map { user ->
                                ChatUiModel(
                                    chatId = chat.chatId,
                                    otherUserId = otherUserId,
                                    otherUserName = user?.name ?: "Unknown",
                                    lastMessage = chat.lastMessage,
                                    lastTimestamp = chat.lastTimestamp,
                                    profileImg = user?.profileImg ?: "",
                                    unreadCount = if (chat.lastMessageSenderId != currentUid) chat.unreadCount else 0
                                )
                            }
                        }
                        combine(flowList) { it.toList() }
                    }
                }
                .catch {
                    _isLoading.value = false
                }
                .collect {
                    _chatListState.value = it
                    _isLoading.value = false
                }
        }
    }
}

data class ChatUiModel(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val profileImg: String,
    val unreadCount: Int
)

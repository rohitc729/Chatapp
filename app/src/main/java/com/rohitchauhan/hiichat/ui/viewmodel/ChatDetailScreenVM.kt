package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitchauhan.hiichat.data.remote.firebase.FirebaseService
import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.domain.use_case.GetMessagesUC
import com.rohitchauhan.hiichat.domain.use_case.MarkMessagesAsReadUC
import com.rohitchauhan.hiichat.domain.use_case.SendMessageUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailScreenVM @Inject constructor(
    private val getMessagesUC: GetMessagesUC,
    private val sendMessageUC: SendMessageUC,
    private val markMessagesAsReadUC: MarkMessagesAsReadUC,
    private val firebaseService: FirebaseService
) : ViewModel() {

    private val _messagesState = MutableStateFlow<List<MessageDto>>(emptyList())
    val messagesState = _messagesState.asStateFlow()

    private val _chatEvent = MutableSharedFlow<ChatEvent>()
    val chatEvent = _chatEvent.asSharedFlow()

    val currentUid = firebaseService.getCurrentUid()

    fun getMessages(chatId: String) {
        viewModelScope.launch {
            getMessagesUC(chatId)
                .catch { e ->
                    _chatEvent.emit(ChatEvent.ShowError(e.message.toString()))
                }
                .collect { messages ->
                    _messagesState.value = messages.asReversed()
                }
        }
    }

    fun markMessagesAsRead(chatId: String) {
        markMessagesAsReadUC(chatId)
    }

    fun sendMessage(chatId: String, receiverId: String, text: String) {
        if (text.isBlank()) return
        
        val senderId = firebaseService.getCurrentUid() ?: return
        val message = MessageDto(
            chatId = chatId,
            senderId = senderId,
            receiverId = receiverId,
            messageText = text,
            timeStamp = System.currentTimeMillis()
        )

        sendMessageUC(
            message = message,
            onSuccess = {
                // Message list updates automatically via flow
            },
            onFailure = { e ->
                viewModelScope.launch {
                    _chatEvent.emit(ChatEvent.ShowError(e.message.toString()))
                }
            }
        )
    }
}

sealed interface ChatEvent {
    data class ShowError(val message: String) : ChatEvent
}

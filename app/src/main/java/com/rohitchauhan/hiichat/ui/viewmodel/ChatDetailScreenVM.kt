package com.rohitchauhan.hiichat.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitchauhan.hiichat.data.remote.firebase.FirebaseService
import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.data.remote.supabase.SupabaseService
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
    private val firebaseService: FirebaseService,
    private val supabaseService: SupabaseService
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

    fun toggleReaction(chatId: String, messageId: String, emoji: String) {
        firebaseService.toggleReaction(chatId, messageId, emoji)
    }

    fun sendMessage(
        chatId: String,
        receiverId: String,
        text: String,
        replyToMessage: MessageDto? = null,
        replySenderName: String? = null
    ) {
        if (text.isBlank()) return
        
        val senderId = firebaseService.getCurrentUid() ?: return
        val message = MessageDto(
            chatId = chatId,
            senderId = senderId,
            receiverId = receiverId,
            messageText = text,
            messageType = "text",
            replyToMessageId = replyToMessage?.messageId,
            replyToMessageText = if (replyToMessage?.messageType == "image") "📷 Image" else replyToMessage?.messageText,
            replyToSenderName = replySenderName,
            replyToMessageType = replyToMessage?.messageType,
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

    fun sendImageMessage(
        context: Context,
        chatId: String,
        receiverId: String,
        imageUri: Uri
    ) {
        val senderId = firebaseService.getCurrentUid() ?: return
        val tempMessageId = "temp_${System.currentTimeMillis()}"

        // Optimistic UI update: local preview immediately
        val tempMessage = MessageDto(
            messageId = tempMessageId,
            chatId = chatId,
            senderId = senderId,
            receiverId = receiverId,
            messageText = imageUri.toString(),
            messageType = "image",
            timeStamp = System.currentTimeMillis()
        )

        _messagesState.value = listOf(tempMessage) + _messagesState.value

        viewModelScope.launch {
            try {
                // 1. Compress & Upload to Supabase Storage (using profile-images bucket)
                val path = "chats/img_${System.currentTimeMillis()}.jpeg"
                val publicUrl = supabaseService.uploadImage(
                    context = context,
                    uri = imageUri,
                    bucketName = "profile-images",
                    path = path
                )

                // 2. Final Message with public URL
                val finalMessage = MessageDto(
                    chatId = chatId,
                    senderId = senderId,
                    receiverId = receiverId,
                    messageText = publicUrl,
                    messageType = "image",
                    timeStamp = System.currentTimeMillis()
                )

                // 3. Save message to Firebase Realtime Database
                sendMessageUC(
                    message = finalMessage,
                    onSuccess = {
                        // Realtime DB sync will update the message list automatically
                    },
                    onFailure = { e ->
                        // Remove optimistic temp message on failure
                        _messagesState.value = _messagesState.value.filter { it.messageId != tempMessageId }
                        viewModelScope.launch {
                            _chatEvent.emit(ChatEvent.ShowError(e.message.toString()))
                        }
                    }
                )
            } catch (e: Exception) {
                // Remove optimistic temp message on upload failure
                _messagesState.value = _messagesState.value.filter { it.messageId != tempMessageId }
                viewModelScope.launch {
                    _chatEvent.emit(ChatEvent.ShowError(e.message ?: "Failed to upload image"))
                }
            }
        }
    }
}

sealed interface ChatEvent {
    data class ShowError(val message: String) : ChatEvent
}

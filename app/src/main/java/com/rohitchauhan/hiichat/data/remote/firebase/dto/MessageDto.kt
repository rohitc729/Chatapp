package com.rohitchauhan.hiichat.data.remote.firebase.dto

import com.google.firebase.database.PropertyName

data class MessageDto(
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val messageText: String = "",
    val messageType: String = "text",
    val replyToMessageId: String? = null,
    val replyToMessageText: String? = null,
    val replyToSenderName: String? = null,
    val replyToMessageType: String? = null,
    val timeStamp: Long = System.currentTimeMillis(),
    val reactions: Map<String, String> = emptyMap(),
    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean = false
)

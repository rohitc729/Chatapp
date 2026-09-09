package com.rohitchauhan.hiichat.data.model

data class ChatModel(
    val chatId:String,
    val members:List<String>,
    val lastMessage:String,
    val lastMessageSenderId:String,
    val lastTimestamp:Long,
    val unreadCount:Int
)

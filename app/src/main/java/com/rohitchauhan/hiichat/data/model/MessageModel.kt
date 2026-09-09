package com.rohitchauhan.hiichat.data.model

data class MessageModel(
    val messageId:String="",
    val senderId:String="",
    val receiverId:String="",
    val messageText:String="",
    val messageType:String="text",
    val timeStamp:Long= System.currentTimeMillis(),
    val isRead: Boolean=false
)

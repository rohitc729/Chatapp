package com.rohitchauhan.hiichat.data.remote.firebase.dto

data class MessageDto(
    val messageId:String="",
    val chatId:String="",
    val senderId:String="",
    val receiverId:String="",
    val messageText:String="",
    val messageType:String="text",
    val timeStamp:Long= System.currentTimeMillis(),
    val isRead: Boolean=false
)

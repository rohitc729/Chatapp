package com.rohitchauhan.hiichat.data.remote.firebase.dto

import com.google.firebase.database.PropertyName


data class MessageDto(
    val messageId:String="",
    val chatId:String="",
    val senderId:String="",
    val receiverId:String="",
    val messageText:String="",
    val messageType:String="text",
    val timeStamp:Long= System.currentTimeMillis(),
    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean=false
)

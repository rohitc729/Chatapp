package com.rohitchauhan.hiichat.data.remote.firebase.dto

data class ChatModel(
    val chatId:String ,
    val members:List<String> ,
    val lastMessage:String ,
    val lastMessageSenderId:String ,
    val lastTimestamp:Long ,
    val unreadCount:Int
){
    constructor() : this("", emptyList(), "", "", 0L, 0)
}

package com.rohitchauhan.hiichat.data.model

data class UserModel(
    val id:Int,
    val name: String,
    val email:String,
    val profileImg: String,
    val lastSeen:Long,
    val fcmToken:String
)

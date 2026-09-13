package com.rohitchauhan.hiichat.data.remote.firebase.dto

data class UserDto(
    val id: String="",
    val name: String="",
    val email:String="",
    val profileImg: String="",
    val lastSeen:Long=0L,
    val fcmToken:String=""
)

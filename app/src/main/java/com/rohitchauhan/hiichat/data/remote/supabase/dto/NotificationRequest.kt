package com.rohitchauhan.hiichat.data.remote.supabase.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationRequest(
    val token: String,
    val title: String,
    val body: String
)

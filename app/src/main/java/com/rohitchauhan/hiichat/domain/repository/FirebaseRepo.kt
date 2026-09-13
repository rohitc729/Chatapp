package com.rohitchauhan.hiichat.domain.repository

interface FirebaseRepo {
    fun signUp(email: String, password: String, name: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
}
package com.rohitchauhan.hiichat.domain.repository

interface FirebaseRepo {
    fun signUp(email: String, password: String, name: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
    fun signin(email: String, password: String,onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
    fun signOut()
    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun signInWithGoogle(idToken: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)

}
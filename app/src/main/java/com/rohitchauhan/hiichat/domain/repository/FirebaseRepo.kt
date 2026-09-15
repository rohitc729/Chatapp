package com.rohitchauhan.hiichat.domain.repository

import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import kotlinx.coroutines.flow.Flow

interface FirebaseRepo {
    fun signUp(email: String, password: String, name: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
    fun signin(email: String, password: String,onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
    fun signOut()
    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun signInWithGoogle(idToken: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
    fun getAllUsers(): Flow<List<UserDto>>
}
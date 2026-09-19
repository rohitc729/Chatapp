package com.rohitchauhan.hiichat.domain.repository

import com.rohitchauhan.hiichat.data.remote.firebase.dto.ChatModel
import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import kotlinx.coroutines.flow.Flow

interface FirebaseRepo {
    fun signUp(email: String, password: String, name: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
    fun signin(email: String, password: String,onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
    fun signOut()
    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun signInWithGoogle(idToken: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)
    fun getAllUsers(): Flow<List<UserDto>>
    fun getUserById(uid: String): Flow<UserDto?>
    fun sendMessage(message: MessageDto, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun getMessages(chatId: String): Flow<List<MessageDto>>
    fun getUserChats(): Flow<List<ChatModel>>
}
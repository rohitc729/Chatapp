package com.rohitchauhan.hiichat.data.repository

import com.rohitchauhan.hiichat.data.remote.firebase.FirebaseService
import com.rohitchauhan.hiichat.data.remote.firebase.dto.ChatModel
import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FireBaseRepoImpl @Inject constructor(
    private val firebaseService: FirebaseService
): FirebaseRepo {
    override fun signUp(
        email: String,
        password: String,
        name: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseService.signUpUser(email, password, name, onSuccess, onFailure)
    }

    override fun signin(
        email: String,
        password: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseService.signIn(email=email,password=password,onSuccess,onFailure)
    }

    override fun signOut() {
       firebaseService.signOut()
    }

    override fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseService.sendPasswordResetEmail(email, onSuccess, onFailure)
    }

    override fun signInWithGoogle(
        idToken: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseService.signInWithGoogle(idToken, onSuccess, onFailure)
    }

    override fun getAllUsers(): Flow<List<UserDto>> {
        return firebaseService.getAllUsers()
    }

    override fun getUserById(uid: String): Flow<UserDto?> {
        return firebaseService.getUserById(uid)
    }

    override fun sendMessage(
        message: MessageDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseService.sendMessage(message, onSuccess, onFailure)
    }

    override fun getMessages(chatId: String): Flow<List<MessageDto>> {
        return firebaseService.getMessages(chatId)
    }

    override fun getUserChats(): Flow<List<ChatModel>> {
        return firebaseService.getUserChats()
    }

}
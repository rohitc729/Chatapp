package com.rohitchauhan.hiichat.data.repository

import com.rohitchauhan.hiichat.data.remote.firebase.FirebaseService
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
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
}
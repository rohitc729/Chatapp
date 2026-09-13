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
}
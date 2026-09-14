package com.rohitchauhan.hiichat.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) {
    //1. Current user id
    fun getCurrentUid(): String? = firebaseAuth.currentUser?.uid

    //2. Signup user
    fun signUpUser(
        email: String,
        password: String,
        name: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                firebaseDatabase.reference.child("users").child(getCurrentUid()!!).setValue(
                    UserDto(
                        id = getCurrentUid()!!,
                        name = name,
                        email = email,
                    )
                ).addOnSuccessListener {
                    updateFcmToken()
                    onSuccess(true)
                }.addOnFailureListener {
                    onFailure(it)
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    //3.Login user
    fun signIn(
        email: String,
        password: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateFcmToken()
                onSuccess(true)
            }
            .addOnFailureListener {onFailure(it)  }
    }

    //4.Logout user
    fun signOut() {
        firebaseAuth.signOut()
    }
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (email.isBlank()) {
            onFailure(Exception("Email cannot be empty"))
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun signInWithGoogle(
        idToken: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    firebaseDatabase.reference.child("users").child(user.uid).setValue(
                        UserDto(
                            id = user.uid,
                            name = user.displayName ?: "",
                            email = user.email ?: "",
                        )
                    ).addOnSuccessListener {
                        updateFcmToken()
                        onSuccess(true)
                    }.addOnFailureListener {
                        onFailure(it)
                    }
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    private fun updateFcmToken() {
        val uid = getCurrentUid() ?: return
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            firebaseDatabase.reference.child("users").child(uid).child("fcmToken").setValue(token)
        }
    }

}
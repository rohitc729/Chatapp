package com.rohitchauhan.hiichat.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
            .addOnSuccessListener { onSuccess(true) }
            .addOnFailureListener {onFailure(it)  }
    }

    //4.Logout user
    fun signOut() {
        firebaseAuth.signOut()
    }


}
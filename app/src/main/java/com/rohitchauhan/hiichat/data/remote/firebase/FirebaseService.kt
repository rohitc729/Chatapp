package com.rohitchauhan.hiichat.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    fun getAllUsers(): Flow<List<UserDto>> = callbackFlow {
        val usersRef = firebaseDatabase.reference.child("users")
        val usersListener =  object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<UserDto>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserDto::class.java)
                    if (user != null && user.id!=getCurrentUid()) {
                        users.add(user)
                    }
                }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
               close(error.toException())
            }
        }
        usersRef.addValueEventListener(usersListener)
        awaitClose {
            usersRef.removeEventListener(usersListener)
        }
    }
    private fun updateFcmToken() {
        val uid = getCurrentUid() ?: return
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            firebaseDatabase.reference.child("users").child(uid).child("fcmToken").setValue(token)
        }
    }

}
package com.rohitchauhan.hiichat.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.rohitchauhan.hiichat.data.remote.firebase.dto.ChatModel
import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) {
    //1. Current user id
    fun getCurrentUid(): String? = firebaseAuth.currentUser?.uid

    fun getChatId(otherUserId: String): String {
        val myUid = getCurrentUid() ?: ""
        return if (myUid < otherUserId) "${myUid}_$otherUserId" else "${otherUserId}_$myUid"
    }

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

    fun getUserById(uid: String): Flow<UserDto?> = callbackFlow {
        val userRef = firebaseDatabase.reference.child("users").child(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(UserDto::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        userRef.addValueEventListener(listener)
        awaitClose { userRef.removeEventListener(listener) }
    }

    //send a message to a user
    fun sendMessage(
        message: MessageDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val chatId = message.chatId
        val messageId = firebaseDatabase.reference.child("messages").child(chatId).push().key ?: return
        val finalMessage = message.copy(messageId = messageId)

        val updates = hashMapOf<String, Any>()
        // 1. Add message to history
        updates["/messages/$chatId/$messageId"] = finalMessage
        
        // 2. Update chat metadata
        updates["/chats/$chatId/lastMessage"] = finalMessage.messageText
        updates["/chats/$chatId/lastMessageSenderId"] = finalMessage.senderId
        updates["/chats/$chatId/lastTimestamp"] = finalMessage.timeStamp
        updates["/chats/$chatId/chatId"] = chatId
        updates["/chats/$chatId/members"] = listOf(finalMessage.senderId, finalMessage.receiverId)
        
        // 3. Update index for both users
        updates["/user_chats/${finalMessage.senderId}/$chatId"] = true
        updates["/user_chats/${finalMessage.receiverId}/$chatId"] = true

        firebaseDatabase.reference.updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getMessages(chatId: String): Flow<List<MessageDto>> = callbackFlow {
        val messagesRef = firebaseDatabase.reference.child("messages").child(chatId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(MessageDto::class.java) }
                trySend(messages)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        messagesRef.addValueEventListener(listener)
        awaitClose { messagesRef.removeEventListener(listener) }
    }

    fun getUserChats(): Flow<List<ChatModel>> = callbackFlow {
        val uid = getCurrentUid() ?: run {
            close(Exception("User not logged in"))
            return@callbackFlow
        }
        val userChatsRef = firebaseDatabase.reference.child("user_chats").child(uid)
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatIds = snapshot.children.mapNotNull { it.key }
                if (chatIds.isEmpty()) {
                    trySend(emptyList())
                    return
                }

                val chats = mutableListOf<ChatModel>()
                var processedCount = 0
                
                chatIds.forEach { chatId ->
                    firebaseDatabase.reference.child("chats").child(chatId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(chatSnapshot: DataSnapshot) {
                                chatSnapshot.getValue(ChatModel::class.java)?.let { chats.add(it) }
                                processedCount++
                                if (processedCount == chatIds.size) {
                                    trySend(chats.sortedByDescending { it.lastTimestamp })
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Log or handle partial failure
                                processedCount++
                                if (processedCount == chatIds.size) {
                                    trySend(chats.sortedByDescending { it.lastTimestamp })
                                }
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        userChatsRef.addValueEventListener(listener)
        awaitClose { userChatsRef.removeEventListener(listener) }
    }

    private fun updateFcmToken() {
        val uid = getCurrentUid() ?: return
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            firebaseDatabase.reference.child("users").child(uid).child("fcmToken").setValue(token)
        }
    }

}
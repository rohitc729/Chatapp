package com.rohitchauhan.hiichat.data.remote.firebase

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.rohitchauhan.hiichat.data.local.room.UserDao
import com.rohitchauhan.hiichat.data.local.room.UserEntity
import com.rohitchauhan.hiichat.data.remote.firebase.dto.ChatModel
import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import com.rohitchauhan.hiichat.data.remote.supabase.SupabaseService
import com.rohitchauhan.hiichat.data.remote.supabase.dto.NotificationRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val supabaseClient: SupabaseClient,
    private val supabaseService: SupabaseService,
    private val userDao: UserDao
) {
    //1. Current user id
    fun getCurrentUid(): String? = firebaseAuth.currentUser?.uid

    fun getChatId(otherUserId: String): String {
        val myUid = getCurrentUid() ?: ""
        return if (myUid < otherUserId) "${myUid}_$otherUserId" else "${otherUserId}_$myUid"
    }

    //2. Signup user (handles image upload via Supabase & stores data in Room local DB)
    fun signUpUser(
        context: Context,
        email: String,
        password: String,
        name: String,
        imageUri: Uri?,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: getCurrentUid() ?: ""
                
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var profileImageUrl = ""
                        if (imageUri != null) {
                            val fileName = "users/$uid/profile_${System.currentTimeMillis()}.jpeg"
                            profileImageUrl = supabaseService.uploadImage(
                                context = context,
                                uri = imageUri,
                                bucketName = "profile-images",
                                path = fileName
                            )
                        }

                        val userDto = UserDto(
                            id = uid,
                            name = name,
                            email = email,
                            profileImg = profileImageUrl
                        )

                        // 1. Save to Firebase Realtime Database
                        firebaseDatabase.reference.child("users").child(uid).setValue(userDto)
                            .await()

                        // 2. Save to local Room database
                        userDao.insertUser(UserEntity.fromUserDto(userDto))

                        // 3. Update FCM token
                        updateFcmToken()

                        withContext(Dispatchers.Main) {
                            onSuccess(true)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onFailure(e)
                        }
                    }
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    //3.Login user (Email/Password) - fetches user from Firebase and updates Room DB
    fun signIn(
        email: String,
        password: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: getCurrentUid()
                if (uid != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val snapshot = firebaseDatabase.reference.child("users").child(uid).get().await()
                            val userDto = snapshot.getValue(UserDto::class.java)
                            if (userDto != null) {
                                userDao.insertUser(UserEntity.fromUserDto(userDto))
                            }
                            updateFcmToken()
                            withContext(Dispatchers.Main) {
                                onSuccess(true)
                            }
                        } catch (e: Exception) {
                            updateFcmToken()
                            withContext(Dispatchers.Main) {
                                onSuccess(true)
                            }
                        }
                    }
                } else {
                    updateFcmToken()
                    onSuccess(true)
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    //4.Logout user
    fun signOut() {
        firebaseAuth.signOut()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userDao.clearUser()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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


    //5. Sign in with Google - fetches/creates user in Firebase and updates Room DB
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
                    val uid = user.uid
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val userRef = firebaseDatabase.reference.child("users").child(uid)
                            val snapshot = userRef.get().await()
                            var userDto = snapshot.getValue(UserDto::class.java)

                            if (userDto == null) {
                                userDto = UserDto(
                                    id = uid,
                                    name = user.displayName ?: "",
                                    email = user.email ?: "",
                                    profileImg = user.photoUrl?.toString() ?: ""
                                )
                                userRef.setValue(userDto).await()
                            }

                            // Save to local Room DB
                            userDao.insertUser(UserEntity.fromUserDto(userDto))

                            updateFcmToken()
                            withContext(Dispatchers.Main) {
                                onSuccess(true)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                onFailure(e)
                            }
                        }
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

        val lastMessageDisplay = if (finalMessage.messageType == "image") "📷 Image" else finalMessage.messageText

        val updates = hashMapOf<String, Any>()
        // 1. Add message to history
        updates["/messages/$chatId/$messageId"] = finalMessage
        
        // 2. Update chat metadata
        updates["/chats/$chatId/lastMessage"] = lastMessageDisplay
        updates["/chats/$chatId/lastMessageSenderId"] = finalMessage.senderId
        updates["/chats/$chatId/lastTimestamp"] = finalMessage.timeStamp
        updates["/chats/$chatId/chatId"] = chatId
        updates["/chats/$chatId/members"] = listOf(finalMessage.senderId, finalMessage.receiverId)
        
        // Atomically increment unread count for the receiver
        updates["/chats/$chatId/unreadCount"] = ServerValue.increment(1)
        
        // 3. Update index for both users
        updates["/user_chats/${finalMessage.senderId}/$chatId"] = true
        updates["/user_chats/${finalMessage.receiverId}/$chatId"] = true

        firebaseDatabase.reference.updateChildren(updates)
            .addOnSuccessListener { 
                onSuccess()
                // Trigger notification in background
                triggerNotification(finalMessage)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun markMessagesAsRead(chatId: String) {
        val myUid = getCurrentUid() ?: return
        val messagesRef = firebaseDatabase.reference.child("messages").child(chatId)
        
        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updates = hashMapOf<String, Any?>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(MessageDto::class.java)
                    if (message != null && message.receiverId == myUid && !message.isRead) {
                        updates["${messageSnapshot.key}/isRead"] = true
                    }
                }
                if (updates.isNotEmpty()) {
                    messagesRef.updateChildren(updates)
                }
                firebaseDatabase.reference.child("chats").child(chatId).child("unreadCount").setValue(0)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun triggerNotification(message: MessageDto) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val receiverSnapshot = firebaseDatabase.reference.child("users").child(message.receiverId).get().await()
                val receiver = receiverSnapshot.getValue(UserDto::class.java)
                val token = receiver?.fcmToken

                if (!token.isNullOrBlank()) {
                    val senderSnapshot = firebaseDatabase.reference.child("users").child(message.senderId).get().await()
                    val sender = senderSnapshot.getValue(UserDto::class.java)
                    
                    val notificationBody = if (message.messageType == "image") "📷 Image" else message.messageText
                    val request = NotificationRequest(
                        token = token,
                        title = sender?.name ?: "New Message",
                        body = notificationBody
                    )
                    
                    supabaseClient.functions.invoke("notify-user", request)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
        
        val chatListeners = mutableMapOf<String, ValueEventListener>()

        val userChatsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentChatIds = snapshot.children.mapNotNull { it.key }.toSet()
                
                val removedChatIds = chatListeners.keys - currentChatIds
                removedChatIds.forEach { id ->
                    firebaseDatabase.reference.child("chats").child(id).removeEventListener(chatListeners[id]!!)
                    chatListeners.remove(id)
                }

                val newChatIds = currentChatIds - chatListeners.keys
                newChatIds.forEach { chatId ->
                    val listener = object : ValueEventListener {
                        override fun onDataChange(chatSnapshot: DataSnapshot) {
                            fetchAllChats(currentChatIds.toList())
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    }
                    firebaseDatabase.reference.child("chats").child(chatId).addValueEventListener(listener)
                    chatListeners[chatId] = listener
                }

                fetchAllChats(currentChatIds.toList())
            }

            private fun fetchAllChats(chatIds: List<String>) {
                if (chatIds.isEmpty()) {
                    trySend(emptyList())
                    return
                }
                
                val chats = mutableListOf<ChatModel>()
                var count = 0
                chatIds.forEach { id ->
                    firebaseDatabase.reference.child("chats").child(id).get().addOnSuccessListener { s ->
                        s.getValue(ChatModel::class.java)?.let { chats.add(it) }
                        count++
                        if (count == chatIds.size) {
                            trySend(chats.sortedByDescending { it.lastTimestamp })
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        userChatsRef.addValueEventListener(userChatsListener)
        
        awaitClose {
            userChatsRef.removeEventListener(userChatsListener)
            chatListeners.forEach { (id, listener) ->
                firebaseDatabase.reference.child("chats").child(id).removeEventListener(listener)
            }
        }
    }

    fun updateFcmToken(token: String) {
        val uid = getCurrentUid() ?: return
        firebaseDatabase.reference.child("users").child(uid).child("fcmToken").setValue(token)
    }

    private fun updateFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            updateFcmToken(token)
        }
    }

}

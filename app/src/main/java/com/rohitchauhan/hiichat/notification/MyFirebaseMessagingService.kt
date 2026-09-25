package com.rohitchauhan.hiichat.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rohitchauhan.hiichat.MainActivity
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseRepo: FirebaseRepo

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        firebaseRepo.updateFcmToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val messageType = message.data["type"]

        if (messageType == "incoming_call") {
            val callerName = message.data["callerName"] ?: "Incoming Call"
            val chatId = message.data["chatId"] ?: ""
            val callerId = message.data["callerId"] ?: ""
            val isVideoCall = message.data["isVideoCall"]?.toBoolean() ?: true
            showIncomingCallNotification(chatId, callerId, callerName, isVideoCall)
        } else {
            // For standard chat messages: Suppress notification if app is in foreground
            if (!isAppInForeground()) {
                val title = message.notification?.title ?: message.data["title"]
                val body = message.notification?.body ?: message.data["body"]

                if (title != null && body != null) {
                    showChatNotification(title, body)
                }
            }
        }
    }

    private fun isAppInForeground(): Boolean {
        return ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    private fun showChatNotification(title: String, body: String) {
        val channelId = "chat_messages"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Chat Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun showIncomingCallNotification(chatId: String, callerId: String, callerName: String, isVideoCall: Boolean) {
        val channelId = "call_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Incoming audio and video call alerts"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Accept Call Pending Intent
        val acceptIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "ACCEPT_CALL")
            putExtra("chatId", chatId)
            putExtra("callerId", callerId)
            putExtra("callerName", callerName)
            putExtra("isVideoCall", isVideoCall)
        }
        val acceptPendingIntent = PendingIntent.getActivity(
            this, 101, acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Hang Up Pending Intent
        val hangUpIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "HANG_UP_CALL")
            putExtra("chatId", chatId)
        }
        val hangUpPendingIntent = PendingIntent.getActivity(
            this, 102, hangUpIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(callerName)
            .setContentText(if (isVideoCall) "Incoming Video Call..." else "Incoming Audio Call...")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setAutoCancel(true)
            .addAction(R.drawable.call_selected, "Accept", acceptPendingIntent)
            .addAction(R.drawable.call_unselected, "Hang up", hangUpPendingIntent)
            .setFullScreenIntent(acceptPendingIntent, true)
            .build()

        notificationManager.notify(1001, notification)
    }
}

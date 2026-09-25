package com.rohitchauhan.hiichat.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohitchauhan.hiichat.ui.viewmodel.CallState
import com.rohitchauhan.hiichat.ui.viewmodel.CallViewModel
import org.webrtc.SurfaceViewRenderer

@Composable
fun CallScreen(
    chatId: String,
    callerId: String,
    receiverId: String,
    callerName: String,
    isVideoCall: Boolean,
    onCallEnded: () -> Unit,
    viewModel: CallViewModel = hiltViewModel()
) {
    val callState by viewModel.callState.collectAsStateWithLifecycle()
    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
    val isVideoEnabled by viewModel.isVideoEnabled.collectAsStateWithLifecycle()

    var remoteRenderer by remember { mutableStateOf<SurfaceViewRenderer?>(null) }
    var localRenderer by remember { mutableStateOf<SurfaceViewRenderer?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val recordAudioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (recordAudioGranted) {
            viewModel.startCall(chatId, callerId, receiverId, callerName, isVideoCall)
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
            )
        )
    }

    LaunchedEffect(callState) {
        if (callState is CallState.Ended) {
            onCallEnded()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Remote Video View
        AndroidView(
            factory = { context ->
                SurfaceViewRenderer(context).apply {
                    viewModel.webRTCClient.initRemoteSurfaceView(this)
                    remoteRenderer = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Local Video View (Floating overlay in top-right)
        if (isVideoCall && isVideoEnabled) {
            AndroidView(
                factory = { context ->
                    SurfaceViewRenderer(context).apply {
                        viewModel.webRTCClient.startLocalVideo(this)
                        localRenderer = this
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 16.dp)
                    .size(width = 120.dp, height = 160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray)
            )
        }

        // Call Info Header
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 24.dp)
        ) {
            Text(
                text = callerName,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when (callState) {
                    is CallState.Calling -> "Calling..."
                    is CallState.Incoming -> "Incoming Call..."
                    is CallState.Connected -> "Connected"
                    is CallState.Ended -> "Call Ended"
                    else -> ""
                },
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        // Bottom Controls Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Toggle Mic Button
            IconButton(
                onClick = { viewModel.toggleAudio() },
                modifier = Modifier
                    .size(56.dp)
                    .background(if (isMuted) Color.Red else Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mute Mic",
                    tint = Color.White
                )
            }

            // Switch Camera Button
            if (isVideoCall) {
                IconButton(
                    onClick = { viewModel.switchCamera() },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = Color.White
                    )
                }

                // Toggle Video Button
                IconButton(
                    onClick = { viewModel.toggleVideo() },
                    modifier = Modifier
                        .size(56.dp)
                        .background(if (!isVideoEnabled) Color.Red else Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Toggle Video",
                        tint = Color.White
                    )
                }
            }

            // End Call Button
            IconButton(
                onClick = { viewModel.endCall() },
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Red, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White
                )
            }
        }
    }
}

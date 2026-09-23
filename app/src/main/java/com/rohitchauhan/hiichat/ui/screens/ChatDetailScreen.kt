package com.rohitchauhan.hiichat.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.components.MessageBubble
import com.rohitchauhan.hiichat.components.MyTextField
import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.ui.viewmodel.ChatDetailScreenVM
import com.rohitchauhan.hiichat.utils.getRandomColor
import java.util.*
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    otherUserId: String,
    otherUserName: String,
    onBackClick: () -> Unit
) {
    val viewModel: ChatDetailScreenVM = hiltViewModel()
    val messages by viewModel.messagesState.collectAsState()
    var textState by remember { mutableStateOf("") }
    val currentUid = viewModel.currentUid

    var isSelectingMode by rememberSaveable { mutableStateOf(false)}
    val itemSelected = remember { mutableStateListOf<String>() }

    var showMoreCallMenu by rememberSaveable { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        viewModel.getMessages(chatId)
    }

    LaunchedEffect(messages) {
        if (messages.any { !it.isRead && it.receiverId == currentUid }) {
            viewModel.markMessagesAsRead(chatId)
        }
    }

    // Auto-scroll is no longer manually needed for initial load or new messages
    // as reverseLayout = true handles anchoring to the bottom.
    // However, if we want to force scroll to the very first item (bottom) on change:
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        containerColor = Color(0xFF2196F3).copy(alpha = .1f)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color.Transparent)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                }
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            shape = CircleShape,
                            color = getRandomColor().copy(alpha = .2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(otherUserName[0].uppercase(), fontSize = 18.sp)
                }
                Text(
                    otherUserName, modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f), overflow = TextOverflow.Ellipsis
                )
                Box() {
                    IconButton(
                        onClick = {
                            showMoreCallMenu = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.call_more),
                            contentDescription = "more call"
                        )
                    }

                    DropdownMenu(
                        expanded = showMoreCallMenu,
                        onDismissRequest = { showMoreCallMenu = false },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Audio call") },
                            onClick = {},
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.call_selected),
                                    contentDescription = "audio call"
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Video call") },
                            onClick = {},
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.videocall),
                                    contentDescription = "video call"
                                )
                            }
                        )
                    }
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(R.drawable.menuicon),
                        contentDescription = "menu"
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(color = Color.Transparent)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Transparent)
                        .padding(horizontal = 8.dp)
                        .padding(top = 8.dp),
                    reverseLayout = true // Latest messages at bottom
                ) {
                    items(messages) { message ->
                        Box(modifier = Modifier.background(
                            color=if (itemSelected.contains(message.messageId)) Color(0x512196F3) else Color.Transparent
                        ).combinedClickable(
                            onClick = {
                                if(itemSelected.isEmpty()){
                                    isSelectingMode=false
                                }
                                if(isSelectingMode) {
                                    if (itemSelected.contains(message.messageId)) {
                                        itemSelected.remove(message.messageId)
                                    } else {
                                        itemSelected.add(message.messageId)
                                    }
                                }

                            },
                            onLongClick = {
                                isSelectingMode=true
                                if (itemSelected.contains(message.messageId)) {
                                    itemSelected.remove(message.messageId)
                                } else {
                                    itemSelected.add(message.messageId)
                                }
                            })
                        ) {
                            MessageBubble(
                                message = message,
                                isMe = message.senderId == currentUid
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(8.dp)
                    .padding(start = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        MyTextField(
                            modifier = Modifier.weight(1f),
                            value = textState,
                            onValueChange = { textState = it },
                            placeHolder = "message...",
                            leadingIcon = null,
                            onLeadingIconClick = {},
                            trailingIcon = null,
                            shape = RoundedCornerShape(12.dp),
                            containerColor = Color.White,
                            singleLine = false,
                            maxLine = 12
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            IconButton(onClick = { /* Handle attachment */ }) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "attachment",
                                    tint = Color.Gray
                                )
                            }
                            AnimatedVisibility(
                                visible = textState.isEmpty(),
                                enter = fadeIn() + expandHorizontally(),
                                exit = fadeOut() + shrinkHorizontally()
                            ) {
                                IconButton(onClick = { /* Handle camera */ }) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "camera",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    viewModel.sendMessage(chatId, otherUserId, textState)
                    textState = ""
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


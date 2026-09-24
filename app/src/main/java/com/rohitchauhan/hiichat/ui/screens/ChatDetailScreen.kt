package com.rohitchauhan.hiichat.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.components.AttachmentMenu
import com.rohitchauhan.hiichat.components.MessageBubble
import com.rohitchauhan.hiichat.components.MyTextField
import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.ui.viewmodel.ChatDetailScreenVM
import com.rohitchauhan.hiichat.ui.viewmodel.ChatEvent
import com.rohitchauhan.hiichat.utils.ChatAttachmentItems
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    otherUserId: String,
    otherUserName: String,
    onBackClick: () -> Unit,
    otherUserImage: String
) {
    val viewModel: ChatDetailScreenVM = hiltViewModel()
    val messages by viewModel.messagesState.collectAsState()
    var textState by remember { mutableStateOf("") }
    val currentUid = viewModel.currentUid

    var isSelectingMode by rememberSaveable { mutableStateOf(false) }
    val itemSelected = remember { mutableStateListOf<String>() }

    var showMoreCallMenu by rememberSaveable { mutableStateOf(false) }
    var replyingToMessage by remember { mutableStateOf<MessageDto?>(null) }
    var reactionTargetMessageId by remember { mutableStateOf<String?>(null) }

    val imageMessages = remember(messages) {
        messages.filter { it.messageType == "image" && it.messageText.isNotBlank() }
    }
    var currentPreviewIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    val context = LocalContext.current
    val listState = rememberLazyListState()
    var showAttachmentMenu by rememberSaveable { mutableStateOf(false) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                viewModel.sendImageMessage(context, chatId, otherUserId, it)
            }
        }

    LaunchedEffect(Unit) {
        viewModel.chatEvent.collect { event ->
            when (event) {
                is ChatEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(chatId) {
        viewModel.getMessages(chatId)
    }

    LaunchedEffect(messages) {
        if (messages.any { !it.isRead && it.receiverId == currentUid }) {
            viewModel.markMessagesAsRead(chatId)
        }
    }

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
            ) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                }
                AsyncImage(
                    model = otherUserImage,
                    contentDescription = "profileimage",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(if (otherUserImage.isEmpty()) 4.dp else 0.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(id = R.drawable.user_unselected),
                    fallback = painterResource(id = R.drawable.user_unselected),
                    error = painterResource(id = R.drawable.user_unselected),
                    contentScale = ContentScale.Crop
                )
                Text(
                    otherUserName,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    overflow = TextOverflow.Ellipsis
                )
                Box {
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Transparent)
                        .padding(horizontal = 8.dp)
                        .padding(top = 8.dp),
                    reverseLayout = true
                ) {
                    items(messages, key = { it.messageId }) { message ->
                        val isSelected = itemSelected.contains(message.messageId)
                        SwipeToReplyContainer(
                            onSwipeToReply = {
                                replyingToMessage = message
                            }
                        ) {
                            MessageBubble(
                                message = message,
                                isMe = message.senderId == currentUid,
                                isSelected = isSelected,
                                onClick = {
                                    if (isSelectingMode) {
                                        if (isSelected) {
                                            itemSelected.remove(message.messageId)
                                        } else {
                                            itemSelected.add(message.messageId)
                                        }
                                        if (itemSelected.isEmpty()) {
                                            isSelectingMode = false
                                        }
                                    } else if (message.messageType == "image" && message.messageText.isNotBlank()) {
                                        val index = imageMessages.indexOfFirst { it.messageId == message.messageId }
                                        if (index != -1) {
                                            currentPreviewIndex = index
                                        }
                                    }
                                },
                                onLongClick = {
                                    if (isSelected) {
                                        itemSelected.remove(message.messageId)
                                    } else {
                                        itemSelected.add(message.messageId)
                                    }
                                    isSelectingMode = itemSelected.isNotEmpty()
                                },
                                onDoubleClick = {
                                    reactionTargetMessageId = message.messageId
                                }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                val activeReactionMessageId = reactionTargetMessageId ?: itemSelected.firstOrNull().takeIf { itemSelected.size == 1 }

                // Floating Emoji Reaction Bar
                AnimatedVisibility(
                    visible = activeReactionMessageId != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmojiReactionPopupBar(
                            onEmojiSelect = { emoji ->
                                activeReactionMessageId?.let { targetId ->
                                    viewModel.toggleReaction(chatId, targetId, emoji)
                                }
                                reactionTargetMessageId = null
                                itemSelected.clear()
                                isSelectingMode = false
                            },
                            onDismiss = {
                                reactionTargetMessageId = null
                                itemSelected.clear()
                                isSelectingMode = false
                            }
                        )
                    }
                }

                // Quoted Reply Bar when replying to a message
                AnimatedVisibility(visible = replyingToMessage != null) {
                    replyingToMessage?.let { reply ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .background(Color(0xFFE3F2FD), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(36.dp)
                                    .background(Color(0xFF2196F3), RoundedCornerShape(2.dp))
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "Replying to ${if (reply.senderId == currentUid) "Yourself" else otherUserName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2196F3)
                                )
                                Text(
                                    text = if (reply.messageType == "image") "📷 Photo" else reply.messageText,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.DarkGray
                                )
                            }
                            IconButton(onClick = { replyingToMessage = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel reply", tint = Color.Gray)
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showAttachmentMenu,
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
                ) {
                    AttachmentMenu(
                        onOptionClick = { option ->
                            showAttachmentMenu = false
                            when (option) {
                                ChatAttachmentItems.DOCUMENT -> {}
                                ChatAttachmentItems.CAMERA -> {}
                                ChatAttachmentItems.GALLERY -> {
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                }

                                ChatAttachmentItems.AUDIO -> {}
                                ChatAttachmentItems.LOCATION -> {}
                                ChatAttachmentItems.CONTACT -> {}
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                                IconButton(onClick = { showAttachmentMenu = !showAttachmentMenu }) {
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
                        if (textState.isNotBlank()) {
                            val replySender = if (replyingToMessage?.senderId == currentUid) "You" else otherUserName
                            viewModel.sendMessage(
                                chatId = chatId,
                                receiverId = otherUserId,
                                text = textState,
                                replyToMessage = replyingToMessage,
                                replySenderName = replySender
                            )
                            textState = ""
                            replyingToMessage = null
                        }
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

    currentPreviewIndex?.let { index ->
        ImagePreviewDialog(
            imageMessages = imageMessages,
            currentIndex = index,
            onIndexChange = { newIndex -> currentPreviewIndex = newIndex },
            onDismiss = { currentPreviewIndex = null },
            onSendReply = { replyText ->
                val imageMsg = imageMessages.getOrNull(index)
                val replySender = if (imageMsg?.senderId == currentUid) "You" else otherUserName
                viewModel.sendMessage(
                    chatId = chatId,
                    receiverId = otherUserId,
                    text = replyText,
                    replyToMessage = imageMsg,
                    replySenderName = replySender
                )
            }
        )
    }
}

@Composable
fun EmojiReactionPopupBar(
    onEmojiSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val quickEmojis = listOf("❤️", "👍", "😂", "😮", "😢", "🙏")
    Surface(
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 6.dp,
        tonalElevation = 2.dp,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            quickEmojis.forEach { emoji ->
                Text(
                    text = emoji,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onEmojiSelect(emoji)
                        }
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun SwipeToReplyContainer(
    onSwipeToReply: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "SwipeToReplyOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > 80f) {
                            onSwipeToReply()
                        }
                        offsetX = 0f
                    },
                    onDragCancel = {
                        offsetX = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (dragAmount > 0 || offsetX > 0) {
                            change.consume()
                            offsetX = (offsetX + dragAmount).coerceIn(0f, 120f)
                        }
                    }
                )
            }
    ) {
        if (animatedOffsetX > 10f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .size(32.dp)
                    .background(
                        color = Color(0xFF2196F3).copy(alpha = (animatedOffsetX / 120f).coerceIn(0.2f, 1f)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = "Reply",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun ImagePreviewDialog(
    imageMessages: List<MessageDto>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSendReply: (String) -> Unit = {}
) {
    if (currentIndex !in imageMessages.indices) return
    val currentMessage = imageMessages[currentIndex]
    var replyTextState by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = currentMessage.messageText,
                contentDescription = "Full Image Preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Left Navigation Button (Older Image)
            if (currentIndex < imageMessages.size - 1) {
                IconButton(
                    onClick = { onIndexChange(currentIndex + 1) },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Image",
                        tint = Color.White
                    )
                }
            }

            // Right Navigation Button (Newer Image)
            if (currentIndex > 0) {
                IconButton(
                    onClick = { onIndexChange(currentIndex - 1) },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Image",
                        tint = Color.White
                    )
                }
            }

            // Top Close Button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 16.dp)
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close preview",
                    tint = Color.White
                )
            }

            // Bottom Reply Bar floating above keyboard
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .imePadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    MyTextField(
                        value = replyTextState,
                        onValueChange = { replyTextState = it },
                        placeHolder = "Reply...",
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        containerColor = Color.White,
                        singleLine = false,
                        maxLine = 4
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (replyTextState.isNotBlank()) {
                            onSendReply(replyTextState)
                            replyTextState = ""
                        }
                    },
                    shape = CircleShape,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF3A4EFB)
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "send reply",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

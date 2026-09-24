package com.rohitchauhan.hiichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.utils.formatTime

@Composable
fun MessageBubble(
    message: MessageDto,
    isMe: Boolean,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onDoubleClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = if (isSelected) Color(0x332196F3) else Color.Transparent)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick
            )
            .padding(vertical = 4.dp, horizontal = 4.dp),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .background(
                    color = if (isMe) Color(0xFF2196F3) else Color.White,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 0.dp,
                        bottomEnd = if (isMe) 0.dp else 16.dp
                    )
                )
                .widthIn(min = 56.dp, max = 280.dp)
                .padding(4.dp)
        ) {
            // Render Quoted Reply Box if this message is a reply
            if (!message.replyToMessageId.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                        .background(
                            color = if (isMe) Color.White.copy(alpha = 0.2f) else Color(0xFFF0F0F0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(28.dp)
                            .background(
                                color = if (isMe) Color.White else Color(0xFF2196F3),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = message.replyToSenderName ?: "Message",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMe) Color.White else Color(0xFF2196F3),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = message.replyToMessageText ?: "",
                            fontSize = 11.sp,
                            color = if (isMe) Color.White.copy(alpha = 0.9f) else Color.DarkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            FlowRow(
                modifier = Modifier
                    .padding(start = 2.dp),
                horizontalArrangement = Arrangement.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (message.messageType == "image") {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        AsyncImage(
                            model = message.messageText,
                            contentDescription = "chat image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.user_selected),
                            fallback = painterResource(R.drawable.user_selected)
                        )
                    }
                } else {
                    Text(
                        text = message.messageText,
                        color = if (isMe) Color.White else Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp, end = 8.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp, end = 4.dp)
                ) {
                    Text(
                        text = formatTime(message.timeStamp),
                        fontSize = 10.sp,
                        color = if (isMe) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        textAlign = TextAlign.End,
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (message.isRead) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Render Emoji Reactions Pill at bottom corner of message bubble
        if (message.reactions.isNotEmpty()) {
            val uniqueEmojis = message.reactions.values.distinct().take(3).joinToString("")
            val reactionCount = message.reactions.size
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp,
                tonalElevation = 1.dp,
                modifier = Modifier
                    .align(if (isMe) Alignment.BottomEnd else Alignment.BottomStart)
                    .offset(
                        x = if (isMe) (-12).dp else 12.dp,
                        y = 6.dp
                    )
            ) {
                Text(
                    text = if (reactionCount > 1) "$uniqueEmojis $reactionCount" else uniqueEmojis,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

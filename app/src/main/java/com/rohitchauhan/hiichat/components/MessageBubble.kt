package com.rohitchauhan.hiichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.utils.formatTime

@Composable
fun MessageBubble(
    message: MessageDto, isMe: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        FlowRow(
            modifier = Modifier
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
                .padding(start = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            if (message.messageType == "image") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                    textAlign = TextAlign.End
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
}

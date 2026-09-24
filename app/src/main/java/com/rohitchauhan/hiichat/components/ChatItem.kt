package com.rohitchauhan.hiichat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.utils.formatTime

@Composable
fun ChatListItem(
    onItemClick: () -> Unit,
    profileImage: String,
    name: String,
    lastMessage: String,
    lastTimestamp: Long = 0L,
    totalUnseenMessages: Int = 0
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onItemClick() }                             ,
        verticalAlignment = Alignment.CenterVertically,

    ) {
        AsyncImage(
            model = profileImage,
            contentDescription = "profileimage",
            modifier = Modifier
                .padding(
                    if (profileImage.isEmpty()) 4.dp else 0.dp
                )
                .size(50.dp)
                .clip(CircleShape),
            placeholder = painterResource(id = R.drawable.user_unselected),
            fallback = painterResource(id = R.drawable.user_unselected),
            error = painterResource(id = R.drawable.user_unselected),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(lastMessage, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(lastTimestamp), 
                fontSize = 12.sp,
                color = if (totalUnseenMessages > 0) Color(0xff2196F3) else Color.Gray
            )
            if (totalUnseenMessages > 0) {
                Card(
                    modifier = Modifier.size(20.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xff2196F3)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("$totalUnseenMessages", color = Color.White, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

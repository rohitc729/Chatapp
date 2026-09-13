package com.rohitchauhan.hiichat.components

import android.content.pm.verify.domain.DomainVerificationUserState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitchauhan.hiichat.R

@Composable
fun ChatListItem(
    onItemClick:()-> Unit,
    profileImage:Int,
    name: String,
    lastMessage: String,
    totalUnseenMessages: Int = 0

) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
        ) {
            Image(
                painter = painterResource(profileImage),
                contentDescription = "avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Column(
            modifier = Modifier.padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(lastMessage, fontSize = 12.sp)
        }
       Column(
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Text("10:02 PM", fontSize = 12.sp)
           if(totalUnseenMessages>0) {
               Card(
                   modifier = Modifier.size(20.dp),
                   shape = CircleShape,
                   colors = CardDefaults.cardColors(
                       containerColor = Color(0xff2196F3)
                   )
               ) {
                   Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                       Text("1", color = Color.White)
                   }
               }
           }
       }
    }
}
val fakeChatList  = listOf(
    Users(name = "Diana", profileImage = R.drawable.avatar_1, lastMessage = "hii", totalUnseenMessages = 2),
    Users(name = "Pratik", profileImage = R.drawable.avatar2, lastMessage = "how are you", totalUnseenMessages = 10),
    Users(name = "Sumit", profileImage = R.drawable.avatar3, lastMessage = "are you coming", totalUnseenMessages = 4),
    Users(name = "Sahil", profileImage = R.drawable.avatar4, lastMessage = "i am fine", totalUnseenMessages = 0),
    Users(name = "Raj chauhan", profileImage = R.drawable.avatar5, lastMessage = "how about you", totalUnseenMessages = 0),
    Users(name = "Comatozze", profileImage = R.drawable.avatar6, lastMessage = "we are playing", totalUnseenMessages = 0),
)
data class Users(
    val name: String,
    val profileImage: Int,
    val lastMessage: String,
    val totalUnseenMessages: Int
)
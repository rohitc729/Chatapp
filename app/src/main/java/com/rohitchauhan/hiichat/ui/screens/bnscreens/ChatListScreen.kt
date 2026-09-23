package com.rohitchauhan.hiichat.ui.screens.bnscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.components.ChatListItem
import com.rohitchauhan.hiichat.components.MyTextField
import com.rohitchauhan.hiichat.components.StoryItem
import com.rohitchauhan.hiichat.ui.viewmodel.ChatListScreenVM
import com.rohitchauhan.hiichat.utils.shimmerEffect

@Composable
fun ChatListScreen(
    onChatClick: (chatId: String, otherUserId: String, otherUserName: String) -> Unit = { _, _, _ -> }
) {
    val viewModel: ChatListScreenVM = hiltViewModel()
    val chatList by viewModel.chatListState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        MyTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            placeHolder = "Search",
            leadingIcon = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "search icon"
                    )
                }
            },
            onLeadingIconClick = {},
            shape = RoundedCornerShape(18.dp),
            containerColor = Color(0x112196F3)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StoryItem(
                    userName = "Your Story",
                    image = R.drawable.user_unselected,
                    onItemClick = {},
                    addIcon = {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
                            shape = CircleShape,
                            modifier = Modifier.size(18.dp),
                            onClick = {}
                        ) {
                            Icon(
                                painterResource(R.drawable.add),
                                contentDescription = "add icon",
                                tint = Color.White,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0x112196F3)
            )
        ) {
            if (isLoading) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(10) {
                        ChatShimmerItem()
                    }
                }
            } else if (chatList.isEmpty()) {
                EmptyChatState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(chatList) { chat ->
                        ChatListItem(
                            onItemClick = {
                                onChatClick(chat.chatId, chat.otherUserId, chat.otherUserName)
                            },
                            profileImage = chat.profileImg, // Default for now
                            name = chat.otherUserName,
                            lastMessage = chat.lastMessage,
                            lastTimestamp = chat.lastTimestamp,
                            totalUnseenMessages = chat.unreadCount
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatShimmerItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(20.dp)
                    .background(color = Color.Gray, shape = RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(14.dp)
                    .background(color = Color.Gray, shape = RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
fun EmptyChatState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color(0xFF3A4EFB).copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Chats Yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your inbox is empty. Start a conversation by tapping the '+' button below!",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

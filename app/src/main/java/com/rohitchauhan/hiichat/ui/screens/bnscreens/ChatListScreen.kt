package com.rohitchauhan.hiichat.ui.screens.bnscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.components.ChatListItem
import com.rohitchauhan.hiichat.components.MyTextField
import com.rohitchauhan.hiichat.components.StoryItem
import com.rohitchauhan.hiichat.components.fakeStories
import com.rohitchauhan.hiichat.ui.viewmodel.ChatListScreenVM

@Composable
fun ChatListScreen(
    onChatClick: (chatId: String, otherUserId: String, otherUserName: String) -> Unit = { _, _, _ -> }
) {
    val viewModel: ChatListScreenVM = hiltViewModel()
    val chatList by viewModel.chatListState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    if (isLoading) {
        // Show loading indicator
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    } else {
        var text by remember { mutableStateOf("") }
        Column(
            modifier = Modifier.fillMaxSize()
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
                items(fakeStories.size) { item ->
                    StoryItem(
                        userName = fakeStories.keys.elementAt(item),
                        image = fakeStories.values.elementAt(item),
                        onItemClick = {

                        }
                    )
                }
            }
            Card(
                modifier = Modifier.fillMaxSize()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x112196F3)
                )
            ) {
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
                            profileImage = if (chat.profileImg.isEmpty()) R.drawable.user_icon else R.drawable.user_icon, // Default for now
                            name = chat.otherUserName,
                            lastMessage = chat.lastMessage,
                            totalUnseenMessages = 0 // Needs implementation in Firebase
                        )
                    }
                }
            }
        }
    }
}
package com.rohitchauhan.hiichat.ui.screens.bnscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.components.ChatListItem
import com.rohitchauhan.hiichat.components.MyBottomBar
import com.rohitchauhan.hiichat.components.MyTextField
import com.rohitchauhan.hiichat.components.StoryItem
import com.rohitchauhan.hiichat.components.fakeChatList
import com.rohitchauhan.hiichat.components.fakeStories

@Composable
fun ChatListScreen() {
    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        MyTextField(
            value = text,
            onValueChange = {text=it},
            modifier = Modifier.fillMaxWidth(),
            placeHolder = "Search",
            leadingIcon = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(painter = painterResource(R.drawable.search), contentDescription = "search icon")
                }
            },
            onLeadingIconClick = {},
            shape=RoundedCornerShape(18.dp),
            containerColor= Color(0x112196F3)
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
            ){
                items(fakeChatList.size){
                    ChatListItem(
                        onItemClick = {},
                        profileImage = fakeChatList[it].profileImage,
                        name=fakeChatList[it].name,
                        lastMessage=fakeChatList[it].lastMessage,
                        totalUnseenMessages = fakeChatList[it].totalUnseenMessages
                    )
                }
            }
        }
    }
}
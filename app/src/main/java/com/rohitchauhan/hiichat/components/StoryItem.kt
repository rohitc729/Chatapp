package com.rohitchauhan.hiichat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rohitchauhan.hiichat.R

@Composable
fun StoryItem(userName: String, image: Int, onItemClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            onClick = onItemClick
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = "avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(userName)
    }
}

val fakeStories: Map<String, Int> = mapOf(
    "You" to R.drawable.avatar6,
    "Diana" to R.drawable.avatar_1,
    "Rocky" to R.drawable.avatar2,
    "Pratik" to R.drawable.avatar4,
    "Sumit" to R.drawable.avatar5,
)
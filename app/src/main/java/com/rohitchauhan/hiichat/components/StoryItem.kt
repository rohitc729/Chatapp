package com.rohitchauhan.hiichat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitchauhan.hiichat.R

@Composable
fun StoryItem(
    userName: String, image: Int, onItemClick: () -> Unit,
    addIcon:@Composable (()-> Unit)?=null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Card(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                onClick = onItemClick
            ) {
                Image(
                    painter = painterResource(image),
                    contentDescription = "avatar",
                    modifier = Modifier.fillMaxSize()
                        .padding(4.dp),
                    contentScale = ContentScale.Crop
                )
            }
            addIcon?.invoke()
        }
        Text(userName, fontSize = 14.sp)
    }
}

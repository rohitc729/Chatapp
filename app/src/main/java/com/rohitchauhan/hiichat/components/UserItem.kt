package com.rohitchauhan.hiichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import com.rohitchauhan.hiichat.utils.getRandomColor

@Composable
fun UserItem(
    user: UserDto,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (user.profileImg.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(shape = CircleShape, color = getRandomColor().copy(alpha = .2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(user.name[0].uppercase(), fontSize = 18.sp)
            }
        } else {

        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(
            onClick = {
                onClick()
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.chat_unselected),
                contentDescription = "",
                modifier = Modifier.size(18.dp)
            )
        }
        TextButton(
            onClick = {},
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF2196F3)
            )
        ) {
            Text("Add")
        }
    }
}
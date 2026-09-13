package com.rohitchauhan.hiichat.ui.screens.bnscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.components.MyTopBar

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box() {
            Image(
                painter = painterResource(R.drawable.avatar5),
                contentDescription = "profile image",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
            Card(
                modifier = Modifier.size(46.dp),
                colors = CardDefaults.cardColors(
                    containerColor =  Color(0xff2196F3)
                )
            ) { }
        }
    }
}
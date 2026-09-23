package com.rohitchauhan.hiichat.ui.screens.bnscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitchauhan.hiichat.R

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
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
                    containerColor = Color(0xff2196F3)
                ),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_edit_24),
                        contentDescription = "edit profile photo icon",
                        tint = Color.White
                    )
                }
            }
        }
        Column(modifier = Modifier.fillMaxSize().padding(start = 32.dp, top = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(R.drawable.user_unselected), contentDescription = "", modifier = Modifier.size(28.dp))
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text("Name", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("Rohit chauhan", fontSize = 16.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(R.drawable.about_icon), contentDescription = "", modifier = Modifier.size(28.dp))
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text("About", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("add about yourself", fontSize = 16.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(R.drawable.call_selected), contentDescription = "", modifier = Modifier.size(28.dp))
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text("Phone", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("+91 7084645838", fontSize = 16.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(R.drawable.link_icon), contentDescription = "", modifier = Modifier.size(28.dp))
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text("Links", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("Add Links", fontSize = 14.sp, color = Color(0xff000000))
                }
            }
        }
    }
}
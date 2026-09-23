package com.rohitchauhan.hiichat.ui.screens.bnscreens

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.ui.viewmodel.ChatListScreenVM

@Composable
fun ProfileScreen(
    viewModel: ChatListScreenVM = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = currentUser?.profileImg,
                contentDescription = "profile image",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                placeholder = painterResource(R.drawable.user_selected),
                fallback = painterResource(id = R.drawable.user_unselected),
                error = painterResource(id = R.drawable.user_unselected),
                contentScale = ContentScale.Crop
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
                    Text(currentUser?.name.takeIf { !it.isNullOrBlank() } ?: "User Name", fontSize = 16.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(R.drawable.about_icon), contentDescription = "", modifier = Modifier.size(28.dp))
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text("Email", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(currentUser?.email.takeIf { !it.isNullOrBlank() } ?: "email@example.com", fontSize = 16.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(R.drawable.call_selected), contentDescription = "", modifier = Modifier.size(28.dp))
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text("Phone", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("Add phone number", fontSize = 16.sp)
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

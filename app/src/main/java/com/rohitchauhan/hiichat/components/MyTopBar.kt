package com.rohitchauhan.hiichat.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitchauhan.hiichat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    title: String,
    onMoreClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp
            )
        },
        // Icons on the right (Search, Settings)
        actions = {
            IconButton(onClick = onMoreClick) {
                Icon(
                    painter = painterResource(R.drawable.menuicon),
                    contentDescription = "menu",
                    tint = Color.Black
                )
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent, // Or your theme color
            titleContentColor = Color.Black
        )
    )
}
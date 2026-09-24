package com.rohitchauhan.hiichat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rohitchauhan.hiichat.utils.ChatAttachmentItems

@Composable
fun AttachmentMenu(onOptionClick: (ChatAttachmentItems) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            )
            {
               AttachmentOption(
                    icon = Icons.Default.Description,
                    label = "Document",
                    color = Color(0xFF7E57C2),
                    onClick = { onOptionClick(ChatAttachmentItems.DOCUMENT) }
                )
                AttachmentOption(
                    icon = Icons.Default.CameraAlt,
                    label = "Camera",
                    color = Color(0xFFEF5350),
                    onClick = { onOptionClick(ChatAttachmentItems.CAMERA) }
                )
              AttachmentOption(
                    icon = Icons.Default.Image,
                    label = "Gallery",
                    color = Color(0xFFEC407A),
                    onClick = { onOptionClick(ChatAttachmentItems.GALLERY) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            )
            {
                AttachmentOption(
                    icon = Icons.Default.Headset,
                    label = "Audio",
                    color = Color(0xFFFFA726),
                    onClick = { onOptionClick(ChatAttachmentItems.AUDIO) }
                )
             AttachmentOption(
                    icon = Icons.Default.LocationOn,
                    label = "Location",
                    color = Color(0xFF66BB6A),
                    onClick = { onOptionClick(ChatAttachmentItems.LOCATION) }
                )
                AttachmentOption(
                    icon = Icons.Default.Person,
                    label = "Contact",
                    color = Color(0xFF42A5F5),
                    onClick = { onOptionClick(ChatAttachmentItems.CONTACT) }
                )
            }
        }
    }
}

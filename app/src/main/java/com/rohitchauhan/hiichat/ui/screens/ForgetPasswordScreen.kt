package com.rohitchauhan.hiichat.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rohitchauhan.hiichat.ui.viewmodel.ForgetPasswordEvent
import com.rohitchauhan.hiichat.ui.viewmodel.ForgetPasswordScreenVM
import kotlinx.atomicfu.atomic

@Composable
fun ForgetPasswordScreen(
    gotoBack: () -> Unit
) {
    val viewModel: ForgetPasswordScreenVM = hiltViewModel()
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Password Reset Link Sent") },
            text = { Text("Please check your email for the password reset link.") },
            confirmButton = {
                TextButton(
                    onClick = gotoBack
                ){
                    Text("Done")
                }
            }
        )
    }
    LaunchedEffect(Unit) {
        viewModel.forgetPasswordEvent.collect { event ->
            when (event) {
                is ForgetPasswordEvent.isLoading -> {
                    isLoading = true
                }

                is ForgetPasswordEvent.NavigateToSignIn -> {
                    showDialog=true
                    isLoading = false
                }

                is ForgetPasswordEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            }
        }
    }
    Box(Modifier.fillMaxSize()) {
        //Top left blue blur box
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val radius = with(density) { 220.dp.toPx() }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF5366FF).copy(alpha = 0.30f),
                        Color(0xFF5366FF).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(0f, 0f),
                    radius = radius
                ),
                center = Offset(0f, 0f),
                radius = radius
            )
        }
        //Bottom right blue blur box
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val radius = with(density) { 220.dp.toPx() }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF5366FF).copy(alpha = 0.30f),
                        Color(0xFF5366FF).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(size.width, size.height),
                    radius = radius
                ),
                center = Offset(size.width, size.height),
                radius = radius
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Enter your email to send a password reset link",
                fontSize = 20.sp,
                fontWeight = FontWeight.W400,
                textAlign = TextAlign.Center
            )
            TextField(
                value = viewModel.email.value,
                onValueChange = { viewModel.email.value = it },
                label = { Text("Email") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.resetPassword()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3A4EFB)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    Text("Send Password Reset Link")
                }
            }
        }
    }
}
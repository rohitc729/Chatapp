package com.rohitchauhan.hiichat.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.ui.viewmodel.SignInEvent
import com.rohitchauhan.hiichat.ui.viewmodel.SignInScreenVM

@Composable
fun SignInScreen(
    gotoSignUpScreen: () -> Unit,
    gotoHomeScreen: () -> Unit,
    gotoForgetPasswordScreen: () -> Unit
) {
    val viewModel: SignInScreenVM = hiltViewModel()
    val signInState = viewModel.signInState.collectAsState().value
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.signInEvent.collect { signInEvent ->
            when (signInEvent) {
                is SignInEvent.isLoading -> {
                    isLoading = true
                }

                is SignInEvent.NavigateToHome -> {
                    gotoHomeScreen()
                    isLoading = false
                }

                is SignInEvent.ShowError -> {
                    Toast.makeText(context, signInEvent.message, Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        //top left blue blur box
        Canvas(
            modifier = Modifier.fillMaxSize()
        )
        {
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
        //main column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.appimage),
                contentDescription = "app logo",
                modifier = Modifier.size(50.dp)
            )
            Text(
                "Sign in",
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                "Sign in to connect to your friends\nwith HiiChat \uFE0F",
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp),
                color = Color.DarkGray
            )
            //Email input container
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            )
            {
                Text(
                    "Email*",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 16.dp),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    TextField(
                        value = signInState.email,
                        onValueChange = {
                            viewModel.onEmailTextChanged(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
//                        cursorColor = Color(0xFF3A4EFB)
                        ),
                        placeholder = { Text("Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_email_24),
                                contentDescription = "email leading icon"
                            )
                        },
                        singleLine = true
                    )
                }
            }
            //Password input container
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Password*", modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 16.dp),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    TextField(
                        value = signInState.password,
                        onValueChange = {
                            viewModel.onPasswordTextChanged(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
//                        cursorColor = Color(0xFF3A4EFB)
                        ),
                        placeholder = { Text("Password") },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    viewModel.isPasswordVisible = !viewModel.isPasswordVisible
                                }
                            ) {
                                Icon(
                                    painter = painterResource(viewModel.passwordTrailingIcon),
                                    contentDescription = "password visibility"
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_password_24),
                                contentDescription = "password leading icon"
                            )
                        },
                        visualTransformation = viewModel.passwordVisualTransformation,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            }
            //forget password text
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "Forgot Password?",
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        .clickable{
                            gotoForgetPasswordScreen()
                        },
                    color = Color(0xFF3A4EFB)
                )
            }
            //sign in button
            Button(
                onClick = {
                    viewModel.signIn()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3A4EFB)
                )
            ) {
                Text("Sign in")
            }
            Spacer(modifier = Modifier.weight(1f))
            //text for ask have any account if no then navigate to sign up screen
            Row(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(
                    text = "Don't have an account?",
                )
                Text(" Sign up", modifier = Modifier.clickable {
                    gotoSignUpScreen()
                }, color = Color(0xFF3A4EFB))
            }
        }
    }
}
package com.rohitchauhan.hiichat.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.ui.viewmodel.SignupEvent
import com.rohitchauhan.hiichat.ui.viewmodel.SignupScreenVM

@Composable
fun SignupScreen(
    gotoSignInScreen: () -> Unit,
    gotoMainScreen: () -> Unit,
) {
    val viewModel: SignupScreenVM = hiltViewModel()
    val signUpstate = viewModel.signUpState.collectAsState().value
    val context = LocalContext.current

    var isLoading by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(Unit) {
            viewModel.signupEvent.collect { signUpEvent ->
                when(signUpEvent) {
                    SignupEvent.NavigateToHome -> {
                        gotoMainScreen()
                        isLoading = false
                    }

                    is SignupEvent.ShowError -> {
                        Toast.makeText(context, signUpEvent.message, Toast.LENGTH_SHORT).show()
                        isLoading = false
                    }

                    is SignupEvent.isLoading -> {
                        isLoading = true
                    }
                }
            }

    }

    Box(modifier = Modifier.fillMaxSize()) {
        //top left blur blue box
        Canvas(modifier = Modifier.fillMaxSize())
        {
            val radius = with(density) { 220.dp.toPx() }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF5366FF).copy(alpha = 0.30f),
                        Color(0xFF5366FF).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    radius = radius,
                    center = Offset(0f, 0f)
                ),
                radius = radius,
                center = Offset(0f, 0f)
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
                "Sign up",
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text("Create your HiiChat account", color = Color.Gray)
            //Name text field
            Column (modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)){
                Text("Name*")
                Card(
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    TextField(
                        value = signUpstate.name,
                        onValueChange = { viewModel.onNameChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        placeholder = { Text("Name") }
                    )
                }
            }

            Column (modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)){
                Text("Email*")
                Card(
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    TextField(
                        value = signUpstate.email,
                        onValueChange = { viewModel.onEmailTextChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        placeholder = { Text("Email") }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Password*")
                    Card(
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        TextField(
                            value = signUpstate.password,
                            onValueChange = { viewModel.onPasswordTextChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(
                                    onClick = {}
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_visibility_24),
                                        contentDescription = "password trailing icon"
                                    )
                                }
                            }
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Confirm Password*")
                    Card(
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        TextField(
                            value = signUpstate.confirmPassword,
                            onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(
                                    onClick = {}
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_visibility_24),
                                        contentDescription = "confirm password trailing icon"
                                    )
                                }
                            }
                        )
                    }
                }
            }
            //signup button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                   viewModel.signUp()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3A4EFB)
                )
            ) {
                Text("Sign up")
            }
            //or text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("or")
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            //continue with Google button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.google),
                        contentDescription = "google icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Continue with Google",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(start = 12.dp),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
            //Already have an account text
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account?")
                Text(" Sign in", color = Color(0xFF3A4EFB), modifier = Modifier.clickable {
                    gotoSignInScreen()
                })
            }
        }
    }
}

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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.ui.viewmodel.SignupEvent
import com.rohitchauhan.hiichat.ui.viewmodel.SignupScreenVM
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun SignupScreen(
    gotoSignInScreen: () -> Unit,
    gotoMainScreen: () -> Unit,
) {
    val viewModel: SignupScreenVM = hiltViewModel()
    val signUpstate = viewModel.signUpState.collectAsState().value
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    var isLoading by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.signupEvent.collect { signUpEvent ->
            when (signUpEvent) {
                is SignupEvent.NavigateToHome -> {
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
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
            //password input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        placeholder = { Text("Password") },
                        trailingIcon = {
                            IconButton(
                                onClick = {viewModel.isPasswordVisible=!viewModel.isPasswordVisible}
                            ) {
                                Icon(painter = painterResource(viewModel.passwordTrailingIcon), contentDescription = "password trailing icon")
                            }
                        },
                        visualTransformation = viewModel.passwordVisualTransformation
                    )
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
                if(isLoading){
                    CircularProgressIndicator()
                }else{
                Text("Sign up")
                }
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
                onClick = {
                    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(context.getString(R.string.default_web_client_id))
                        .setAutoSelectEnabled(true)
                        .build()

                    val request: GetCredentialRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    scope.launch {
                        try {
                            val result = credentialManager.getCredential(
                                request = request,
                                context = context,
                            )
                            val credential = result.credential
                            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                viewModel.signInWithGoogle(googleIdTokenCredential.idToken)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(1.dp)
            )
            {
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

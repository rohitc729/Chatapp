package com.rohitchauhan.hiichat.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.ui.navigation.AuthRouts
import com.rohitchauhan.hiichat.ui.navigation.RootGraph
import com.rohitchauhan.hiichat.ui.viewmodel.SplashScreenVM
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    navigateToNextScreen: () -> Unit
) {
    val viewModel: SplashScreenVM = hiltViewModel()
    //delay 1.5s before navigating to next screen(Signup or HomeScreen)
    LaunchedEffect(Unit) {
        delay(1000.milliseconds)
        navigateToNextScreen()
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painterResource(R.drawable.appimage),
            contentDescription = "app logo",
            modifier = Modifier.size(150.dp)
        )
    }
}
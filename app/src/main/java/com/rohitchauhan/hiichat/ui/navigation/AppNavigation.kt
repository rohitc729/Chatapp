package com.rohitchauhan.hiichat.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.google.firebase.auth.FirebaseAuth
import com.rohitchauhan.hiichat.ui.screens.AddChatScreen
import com.rohitchauhan.hiichat.ui.screens.CallScreen
import com.rohitchauhan.hiichat.ui.screens.ChatDetailScreen
import com.rohitchauhan.hiichat.ui.screens.ForgetPasswordScreen
import com.rohitchauhan.hiichat.ui.screens.MainScreen
import com.rohitchauhan.hiichat.ui.screens.SignInScreen
import com.rohitchauhan.hiichat.ui.screens.SignupScreen
import com.rohitchauhan.hiichat.ui.screens.SplashScreen
import com.rohitchauhan.hiichat.ui.viewmodel.SplashScreenVM
import androidx.navigation.toRoute

@Composable
fun AppNavigation(
    callIntent: Intent? = null
) {
    val navController = rememberNavController()

    // Handle notification intent actions (ACCEPT_CALL)
    LaunchedEffect(callIntent) {
        val action = callIntent?.getStringExtra("action")
        if (action == "ACCEPT_CALL") {
            val chatId = callIntent.getStringExtra("chatId") ?: ""
            val callerId = callIntent.getStringExtra("callerId") ?: ""
            val callerName = callIntent.getStringExtra("callerName") ?: "Incoming Call"
            val isVideoCall = callIntent.getBooleanExtra("isVideoCall", true)
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (chatId.isNotBlank()) {
                navController.navigate(
                    MainRouts.CallingScreen(
                        chatId = chatId,
                        callerId = callerId,
                        receiverId = currentUid,
                        callerName = callerName,
                        isVideoCall = isVideoCall
                    )
                )
            }
        }
    }

    NavHost(navController = navController, startDestination = RootGraph.AuthGraph) {
        authGraph(navController)
        mainGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<RootGraph.AuthGraph>(startDestination = AuthRouts.SplashRout) {
        composable<AuthRouts.SplashRout>() {
            val viewModel: SplashScreenVM = hiltViewModel()
            SplashScreen {
                if (viewModel.isLogged()) {
                    navController.navigate(RootGraph.MainGraph){
                        popUpTo(RootGraph.AuthGraph){inclusive=true}
                    }
                } else {
                    navController.navigate(AuthRouts.LoginRout){
                        popUpTo(AuthRouts.SplashRout){inclusive=false}
                    }
                }
            }
        }
        composable<AuthRouts.LoginRout>() {
            SignInScreen(
                gotoHomeScreen = {
                    navController.navigate(RootGraph.MainGraph){
                        popUpTo(RootGraph.AuthGraph){inclusive=true}
                    }
                },
                gotoSignUpScreen = {
                    navController.navigate(AuthRouts.SignupRout)
                },
                gotoForgetPasswordScreen = {
                    navController.navigate(AuthRouts.ForgetPasswordRout)
                }
            )
        }
        composable<AuthRouts.ForgetPasswordRout> {
            ForgetPasswordScreen {
                navController.popBackStack()
            }
        }
        composable<AuthRouts.SignupRout>() {
            SignupScreen(
                gotoMainScreen = {
                    navController.navigate(RootGraph.MainGraph){
                        popUpTo(RootGraph.AuthGraph){inclusive=true}
                    }
                },
                gotoSignInScreen = {
                    navController.navigate(AuthRouts.LoginRout){
                        popUpTo(AuthRouts.LoginRout){inclusive=false}
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation<RootGraph.MainGraph>(startDestination = MainRouts.MainScreen) {
        composable<MainRouts.MainScreen> {
            MainScreen(
                navController = navController,
                onSignOut = {
                    navController.navigate(AuthRouts.LoginRout) {
                        popUpTo(RootGraph.MainGraph) { inclusive = true }
                    }
                },
                gotoAddChatScreen = {
                    navController.navigate(MainRouts.AddChatScreen)
                }
            )
        }
        composable<MainRouts.AddChatScreen> {
            AddChatScreen(
                onUserClick = { chatId, otherUserId, otherUserName ,otherUserImage->
                    navController.navigate(MainRouts.ChatDetailScreen(chatId, otherUserId, otherUserName,otherUserImage)) {
                        popUpTo<MainRouts.AddChatScreen> {
                            inclusive = true
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<MainRouts.ChatDetailScreen> { backStackEntry ->
            val chatDetail: MainRouts.ChatDetailScreen = backStackEntry.toRoute()
            ChatDetailScreen(
                chatId = chatDetail.chatId,
                otherUserId = chatDetail.otherUserId,
                otherUserName = chatDetail.otherUserName,
                otherUserImage = chatDetail.otherUserImage,
                onBackClick = {
                    navController.popBackStack()
                },
                onCallClick = { isVideoCall ->
                    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    navController.navigate(
                        MainRouts.CallingScreen(
                            chatId = chatDetail.chatId,
                            callerId = currentUid,
                            receiverId = chatDetail.otherUserId,
                            callerName = chatDetail.otherUserName,
                            isVideoCall = isVideoCall
                        )
                    )
                }
            )
        }
        composable<MainRouts.CallingScreen> { backStackEntry ->
            val callArgs: MainRouts.CallingScreen = backStackEntry.toRoute()
            CallScreen(
                chatId = callArgs.chatId,
                callerId = callArgs.callerId,
                receiverId = callArgs.receiverId,
                callerName = callArgs.callerName,
                isVideoCall = callArgs.isVideoCall,
                onCallEnded = {
                    navController.popBackStack()
                }
            )
        }
    }
}

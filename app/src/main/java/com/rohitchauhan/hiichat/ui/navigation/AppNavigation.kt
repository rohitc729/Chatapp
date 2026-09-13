package com.rohitchauhan.hiichat.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.rohitchauhan.hiichat.ui.screens.ForgetPasswordScreen
import com.rohitchauhan.hiichat.ui.screens.MainScreen
import com.rohitchauhan.hiichat.ui.screens.SignInScreen
import com.rohitchauhan.hiichat.ui.screens.SignupScreen
import com.rohitchauhan.hiichat.ui.screens.SplashScreen
import com.rohitchauhan.hiichat.ui.viewmodel.SplashScreenVM

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
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
                    navController.navigate(RootGraph.MainGraph)
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
        composable < AuthRouts.ForgetPasswordRout>{
            ForgetPasswordScreen{
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
                onSignOut = {
                    navController.navigate(AuthRouts.LoginRout) {
                        popUpTo(RootGraph.MainGraph) { inclusive = true }
                    }
                }
            )
        }
    }
}
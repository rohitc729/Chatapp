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
import com.rohitchauhan.hiichat.ui.screens.SignInScreen
import com.rohitchauhan.hiichat.ui.screens.SignupScreen
import com.rohitchauhan.hiichat.ui.screens.SplashScreen
import com.rohitchauhan.hiichat.ui.viewmodel.SplashScreenVM

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = RootGraph.AuthGraph,modifier=modifier) {
        authGraph(navController)
        mainGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<RootGraph.AuthGraph>(startDestination = AuthRouts.LoginRout) {
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
            SignInScreen{
                navController.navigate(AuthRouts.SignupRout)
            }
        }
        composable<AuthRouts.SignupRout>() {
            SignupScreen{
                navController.navigate(AuthRouts.LoginRout){
                    popUpTo(AuthRouts.LoginRout){inclusive=false}
                }
            }
        }
    }
}

private fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation<RootGraph.MainGraph>(startDestination = MainRouts.ChatListRout) {
        composable<MainRouts.ChatListRout> { }
    }
}
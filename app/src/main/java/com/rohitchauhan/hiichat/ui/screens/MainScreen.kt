package com.rohitchauhan.hiichat.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rohitchauhan.hiichat.components.MyBottomBar
import com.rohitchauhan.hiichat.components.MyTopBar
import com.rohitchauhan.hiichat.ui.navigation.SubRouts
import com.rohitchauhan.hiichat.ui.screens.bnscreens.CallsScreen
import com.rohitchauhan.hiichat.ui.screens.bnscreens.ChatListScreen
import com.rohitchauhan.hiichat.ui.screens.bnscreens.ProfileScreen
import com.rohitchauhan.hiichat.ui.viewmodel.MainScreenVM

@Composable
fun MainScreen() {
    val subNavController = rememberNavController()
    val viewModel : MainScreenVM= hiltViewModel()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MyBottomBar(
                subNavController,
                onItemClick={
                    viewModel.topBarTitle.value=it
                }
            )
        },
        topBar = {
            MyTopBar(
                onMoreClick = {},
                onSearchClick = {},
                title = viewModel.topBarTitle.value
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = subNavController,
            startDestination = SubRouts.ChatListRout.rout,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(SubRouts.ChatListRout.rout) { ChatListScreen() }
            composable(SubRouts.CallRout.rout) { CallsScreen() }
            composable(SubRouts.ProfileRout.rout) { ProfileScreen() }
        }
    }
}
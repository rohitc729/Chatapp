package com.rohitchauhan.hiichat.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.rohitchauhan.hiichat.R

@Composable
fun MainScreen(
    onSignOut: () -> Unit = {},
    gotoAddChatScreen: () -> Unit = {}
) {
    val subNavController = rememberNavController()
    val viewModel: MainScreenVM = hiltViewModel()
    var showMenu by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MyBottomBar(
                subNavController,
                onItemClick = {
                    viewModel.topBarTitle.value = it
                }
            )
        },
        topBar = {
            MyTopBar(
                title = viewModel.topBarTitle.value,
                onMoreClick = {
                    showMenu = true
                },
                showMenu = showMenu,
                onDismissMenu = { showMenu = false },
                menuContent = {
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        onClick = {
                            showMenu = false
                            viewModel.signOut()
                            onSignOut()
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    gotoAddChatScreen()
                },
                shape = CircleShape,
                contentColor = Color(0xFF001AFF)
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "add icon",
                    tint = Color.White
                )
            }
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
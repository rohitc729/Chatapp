package com.rohitchauhan.hiichat.utils

import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.ui.navigation.SubRouts

val bottomBarItems = listOf(
    BottomBarItem(level = "Chat", rout = SubRouts.ChatListRout.rout, icon = R.drawable.chat_icon),
    BottomBarItem(level = "Calls", rout = SubRouts.CallRout.rout, icon = R.drawable.baseline_phone_24),
    BottomBarItem(level = "Profile", rout = SubRouts.ProfileRout.rout, icon = R.drawable.user),
)

data class BottomBarItem(
    val level: String,
    val rout:String,
    val icon : Int
)
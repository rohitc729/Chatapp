package com.rohitchauhan.hiichat.utils

import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.ui.navigation.SubRouts

val bottomBarItems = listOf(
    BottomBarItem(level = "Chat", rout = SubRouts.ChatListRout.rout, selectedIcon = R.drawable.chat_selected, unSelectedIcon = R.drawable.chat_unselected),
    BottomBarItem(level = "Calls", rout = SubRouts.CallRout.rout, selectedIcon = R.drawable.call_selected, unSelectedIcon = R.drawable.call_unselected),
    BottomBarItem(level = "Profile", rout = SubRouts.ProfileRout.rout, selectedIcon = R.drawable.user_selected, unSelectedIcon = R.drawable.user_unselected),
)

data class BottomBarItem(
    val level: String,
    val rout:String,
    val selectedIcon : Int,
    val unSelectedIcon : Int
)
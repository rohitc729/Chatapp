package com.rohitchauhan.hiichat.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color


val listofColor = listOf(
    Color.DarkGray,
    Color.Gray,
    Color.LightGray,
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Yellow,
    Color.Cyan,
    Color.Magenta,
)
fun getRandomColor(): Color{
    return listofColor.random()
}
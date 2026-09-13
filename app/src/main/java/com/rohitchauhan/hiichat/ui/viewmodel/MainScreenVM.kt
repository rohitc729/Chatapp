package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenVM @Inject constructor(

): ViewModel() {
    var topBarTitle = mutableStateOf("Chats")
}
package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenVM @Inject constructor(
    private val firebaseRepo: FirebaseRepo
): ViewModel() {
    var topBarTitle = mutableStateOf("Chats")

    fun signOut() {
        firebaseRepo.signOut()
    }
}
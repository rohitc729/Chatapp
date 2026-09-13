package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.rohitchauhan.hiichat.data.remote.firebase.FirebaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenVM @Inject constructor(
    private val firebaseService: FirebaseService
): ViewModel() {
    fun isLogged(): Boolean{
        return firebaseService.getCurrentUid()!=null
    }
}
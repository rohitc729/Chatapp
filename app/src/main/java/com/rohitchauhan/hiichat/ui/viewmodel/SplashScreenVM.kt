package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenVM @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {
    fun isLogged(): Boolean{
        return firebaseAuth.currentUser?.uid!=null
    }
}
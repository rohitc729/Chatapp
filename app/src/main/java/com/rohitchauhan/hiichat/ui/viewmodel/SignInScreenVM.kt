package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModel
import com.rohitchauhan.hiichat.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInScreenVM @Inject constructor(): ViewModel() {
    private val _signInState = MutableStateFlow(SignInState())
    val signInState = _signInState.asStateFlow()

    var isPasswordVisible by  mutableStateOf(false)
    val passwordTrailingIcon =
        if (isPasswordVisible) R.drawable.outline_visibility_off_24 else R.drawable.outline_visibility_24
    val passwordVisualTransformation =
        if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()


    fun onEmailTextChanged(email: String){
        _signInState.value = _signInState.value.copy(email = email)
    }
    fun onPasswordTextChanged(password: String){
        _signInState.value = _signInState.value.copy(password = password)
    }
}
data class SignInState(
    val email:String="",
    val password:String="",
    val isLoading: Boolean =false,
    val errorMessage: String?=null
)
package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.domain.use_case.SignInUC
import com.rohitchauhan.hiichat.domain.use_case.SignInWithGoogleUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInScreenVM @Inject constructor(
    private val signInUC: SignInUC,
    private val signInWithGoogleUC: SignInWithGoogleUC
): ViewModel() {
    private val _signInState = MutableStateFlow(SignInState())
    val signInState = _signInState.asStateFlow()
    private val _signInEvent = MutableSharedFlow<SignInEvent>() // No initial value needed
    val signInEvent = _signInEvent.asSharedFlow()

    var isPasswordVisible by  mutableStateOf(false)
    val passwordTrailingIcon
        get() = if (isPasswordVisible) R.drawable.outline_visibility_off_24 else R.drawable.outline_visibility_24
    val passwordVisualTransformation
        get() = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()

    fun onEmailTextChanged(email: String){
        _signInState.value = _signInState.value.copy(email = email)
    }
    fun onPasswordTextChanged(password: String){
        _signInState.value = _signInState.value.copy(password = password)
    }

    fun signIn() {
        viewModelScope.launch {
            _signInEvent.emit(SignInEvent.isLoading)
            try {
                signInUC(
                    email = signInState.value.email,
                    password = signInState.value.password,
                    onSuccess = {
                        viewModelScope.launch {
                            _signInEvent.emit(SignInEvent.NavigateToHome)
                        }
                    },
                    onFailure = {
                        viewModelScope.launch {
                            _signInEvent.emit(SignInEvent.ShowError(it.message.toString()))
                        }
                    }
                )
            } catch (e: Exception) {
                _signInEvent.emit(SignInEvent.ShowError(e.message.toString()))
            }
        }
    }
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _signInEvent.emit(SignInEvent.isLoading)
                signInWithGoogleUC(
                    idToken = idToken,
                    onSuccess = {
                        viewModelScope.launch {
                            _signInEvent.emit(SignInEvent.NavigateToHome)
                        }
                    },
                    onFailure = {
                        viewModelScope.launch {
                            _signInEvent.emit(SignInEvent.ShowError(it.message.toString()))
                        }
                    }
                )
            } catch (e: Exception) {
                _signInEvent.emit(SignInEvent.ShowError(e.message.toString()))
            }
        }
    }
}
data class SignInState(
    val email:String="",
    val password:String="",
)

sealed interface SignInEvent {
    data object NavigateToHome : SignInEvent
    data class ShowError(val message: String) : SignInEvent
    data object isLoading : SignInEvent
}
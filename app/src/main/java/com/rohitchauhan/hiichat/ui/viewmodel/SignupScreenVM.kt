package com.rohitchauhan.hiichat.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.domain.use_case.SignInWithGoogleUC
import com.rohitchauhan.hiichat.domain.use_case.SignUpUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupScreenVM @Inject constructor(
    private val signUpUC: SignUpUC,
    private val signInWithGoogleUC: SignInWithGoogleUC
) : ViewModel() {
    private val _signUpState = MutableStateFlow(SignUpState())
    val signUpState = _signUpState.asStateFlow()

    private val _signupEvent = MutableSharedFlow<SignupEvent>() // No initial value needed
    val signupEvent = _signupEvent.asSharedFlow()

    var isPasswordVisible by mutableStateOf(false)
    val passwordTrailingIcon
        get() = if (isPasswordVisible) R.drawable.outline_visibility_off_24 else R.drawable.outline_visibility_24
    val passwordVisualTransformation
        get() = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()


    fun onEmailTextChanged(email: String) {
        _signUpState.value = _signUpState.value.copy(email = email)
    }

    fun onPasswordTextChanged(password: String) {
        _signUpState.value = _signUpState.value.copy(
            password = password,
            passwordError = false,
            passwordErrorMessage = null
        )
    }

    fun onNameChanged(name: String) {
        _signUpState.value = _signUpState.value.copy(name = name)
    }

    fun onProfileImageSelected(uri: Uri?) {
        _signUpState.value = _signUpState.value.copy(profileImg = uri)
    }

    fun setPasswordError(isError: Boolean, message: String?) {
        _signUpState.value = _signUpState.value.copy(
            passwordError = isError,
            passwordErrorMessage = message
        )
    }


    fun signUp(context: Context) {
        viewModelScope.launch {
            try {
                _signupEvent.emit(SignupEvent.isLoading)
                signUpUC(
                    context = context,
                    email = _signUpState.value.email,
                    password = _signUpState.value.password,
                    name = _signUpState.value.name,
                    imageUri = _signUpState.value.profileImg,
                    onSuccess = {
                        viewModelScope.launch {
                            _signupEvent.emit(SignupEvent.NavigateToHome)
                        }
                    },
                    onFailure = {
                        viewModelScope.launch {
                            _signupEvent.emit(SignupEvent.ShowError(it))
                        }
                    }
                )
            } catch (e: Exception) {
                _signupEvent.emit(SignupEvent.ShowError(e))
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _signupEvent.emit(SignupEvent.isLoading)
                signInWithGoogleNav(idToken)
            } catch (e: Exception) {
                _signupEvent.emit(SignupEvent.ShowError(e))
            }
        }
    }

    private suspend fun signInWithGoogleNav(idToken: String) {
        signInWithGoogleUC(
            idToken = idToken,
            onSuccess = {
                viewModelScope.launch {
                    _signupEvent.emit(SignupEvent.NavigateToHome)
                }
            },
            onFailure = {
                viewModelScope.launch {
                    _signupEvent.emit(SignupEvent.ShowError(it))
                }
            }
        )
    }
}

data class SignUpState(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var profileImg: Uri? = null,
    val isLoading: Boolean = false,
    val passwordError: Boolean = false,
    val passwordErrorMessage: String? = null
)

sealed interface SignupEvent {
    data object NavigateToHome : SignupEvent
    data class ShowError(val exception: Exception) : SignupEvent
    data object isLoading : SignupEvent
}

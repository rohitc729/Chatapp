package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignupScreenVM @Inject constructor(

) : ViewModel() {
    private val _signUpState = MutableStateFlow(SignUpState())
    val signUpState = _signUpState.asStateFlow()


    fun onEmailTextChanged(email: String){
        _signUpState.value = _signUpState.value.copy(email = email)
    }
    fun onPasswordTextChanged(password: String){
        _signUpState.value = _signUpState.value.copy(password = password)
    }

    fun onFirstNameChanged(firstName: String) {
        _signUpState.value = _signUpState.value.copy(firstName = firstName)
    }
    fun onLastNameChanged(lastName: String) {
        _signUpState.value = _signUpState.value.copy(lastName = lastName)
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _signUpState.value = _signUpState.value.copy(confirmPassword=confirmPassword)
    }
}

data class SignUpState(
    var firstName:String="",
    var lastName:String="",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",
    var isLoading: Boolean = false,
    var errorMessage: String? = null
)

sealed interface SignupEvent {
    data object NavigateToHome : SignupEvent
    data class ShowError(val message: String) : SignupEvent
}
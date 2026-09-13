package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.collection.emptyIntSet
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitchauhan.hiichat.domain.use_case.ResetPasswordUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordScreenVM @Inject constructor(
    private val resetPasswordUC: ResetPasswordUC
): ViewModel() {
    private val _forgetPasswordEvent = MutableSharedFlow<ForgetPasswordEvent>()
    val forgetPasswordEvent = _forgetPasswordEvent.asSharedFlow()

    val email = mutableStateOf("")
    fun resetPassword(){
        viewModelScope.launch {
            _forgetPasswordEvent.emit(ForgetPasswordEvent.isLoading)
            resetPasswordUC(
                email = email.value,
                onSuccess = {
                    viewModelScope.launch {
                        _forgetPasswordEvent.emit(ForgetPasswordEvent.NavigateToSignIn)
                    }
                },
                onFailure = {
                    viewModelScope.launch {
                        _forgetPasswordEvent.emit(ForgetPasswordEvent.ShowError(it.message.toString()))
                    }
                }
            )
        }
    }
}
sealed interface ForgetPasswordEvent{
    data class ShowError(val message: String): ForgetPasswordEvent
    data object NavigateToSignIn: ForgetPasswordEvent
    data object isLoading: ForgetPasswordEvent

}
package com.rohitchauhan.hiichat.domain.use_case

import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import javax.inject.Inject

class ResetPasswordUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
){
    operator fun invoke(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = firebaseRepo.sendPasswordResetEmail(email, onSuccess, onFailure)

}
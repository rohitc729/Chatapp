package com.rohitchauhan.hiichat.domain.use_case

import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import javax.inject.Inject

class SignInWithGoogleUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {
    operator fun invoke(
        idToken: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseRepo.signInWithGoogle(idToken, onSuccess, onFailure)
    }
}

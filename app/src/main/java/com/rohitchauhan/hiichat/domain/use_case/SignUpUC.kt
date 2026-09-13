package com.rohitchauhan.hiichat.domain.use_case

import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import javax.inject.Inject

class SignUpUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {
    operator fun invoke(
        email: String,
        password: String,
        name: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        if(email.isBlank() || password.isBlank() || name.isBlank()){
            throw Exception("Please fill all the fields")
        }else {
            firebaseRepo.signUp(
                email = email,
                password = password,
                name = name,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }
}
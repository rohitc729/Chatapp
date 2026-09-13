package com.rohitchauhan.hiichat.domain.use_case

import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import javax.inject.Inject

class SignInUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {
    operator fun invoke(
        email: String,
        password: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        if(password.isEmpty() || email.isEmpty()){
            throw Exception("Please fill all the fields")
        }else {
            firebaseRepo.signin(
                email = email,
                password = password,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }
}
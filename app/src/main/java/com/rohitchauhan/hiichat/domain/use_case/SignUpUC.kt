package com.rohitchauhan.hiichat.domain.use_case

import android.content.Context
import android.net.Uri
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import javax.inject.Inject

class SignUpUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {
    operator fun invoke(
        context: Context,
        email: String,
        password: String,
        name: String,
        imageUri: Uri?,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        if(email.isBlank() || password.isBlank() || name.isBlank()){
            throw Exception("Please fill all the fields")
        }else if(password.length<6){
            throw PasswordException()
        }
        else {
            firebaseRepo.signUp(
                context = context,
                email = email,
                password = password,
                name = name,
                imageUri = imageUri,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }
}

class PasswordException: Exception("Password must be 6 character")

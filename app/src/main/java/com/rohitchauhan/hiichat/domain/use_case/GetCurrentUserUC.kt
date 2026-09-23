package com.rohitchauhan.hiichat.domain.use_case

import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {
    operator fun invoke(): Flow<UserDto?> {
        return firebaseRepo.getCurrentUser()
    }
}

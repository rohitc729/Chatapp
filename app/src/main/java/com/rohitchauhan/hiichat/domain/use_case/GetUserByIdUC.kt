package com.rohitchauhan.hiichat.domain.use_case

import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByIdUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {
    operator fun invoke(uid: String): Flow<UserDto?> {
        return firebaseRepo.getUserById(uid)
    }
}

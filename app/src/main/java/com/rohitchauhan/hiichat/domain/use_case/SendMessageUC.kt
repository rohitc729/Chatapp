package com.rohitchauhan.hiichat.domain.use_case

import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import javax.inject.Inject

class SendMessageUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {
    operator fun invoke(
        message: MessageDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseRepo.sendMessage(message, onSuccess, onFailure)
    }
}

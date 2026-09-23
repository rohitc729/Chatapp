package com.rohitchauhan.hiichat.domain.use_case

import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import javax.inject.Inject

class MarkMessagesAsReadUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {
    operator fun invoke(chatId: String) {
        firebaseRepo.markMessagesAsRead(chatId)
    }
}

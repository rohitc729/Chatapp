package com.rohitchauhan.hiichat.domain.use_case

import com.rohitchauhan.hiichat.data.remote.firebase.dto.MessageDto
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {
    operator fun invoke(chatId: String): Flow<List<MessageDto>> {
        return firebaseRepo.getMessages(chatId)
    }
}

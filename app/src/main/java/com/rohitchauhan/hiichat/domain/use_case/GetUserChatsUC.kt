package com.rohitchauhan.hiichat.domain.use_case

import com.rohitchauhan.hiichat.data.remote.firebase.dto.ChatModel
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserChatsUC @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {
    operator fun invoke(): Flow<List<ChatModel>> {
        return firebaseRepo.getUserChats()
    }
}

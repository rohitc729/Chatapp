package com.rohitchauhan.hiichat.ui.navigation

import kotlinx.serialization.Serializable

sealed class RootGraph{
    @Serializable
    object AuthGraph: RootGraph()
    @Serializable
    object MainGraph: RootGraph()
}

sealed class AuthRouts{
    @Serializable
    object SplashRout: AuthRouts()
    @Serializable
    object LoginRout: AuthRouts()
    @Serializable
    object SignupRout: AuthRouts()
    @Serializable
    object ForgetPasswordRout: AuthRouts()
}
sealed class MainRouts{
    @Serializable
    object MainScreen: MainRouts()
    @Serializable
    object AddChatScreen: MainRouts()
    @Serializable
    data class ChatDetailScreen(
        val chatId: String,
        val otherUserId: String,
        val otherUserName: String
    ): MainRouts()
}
@Serializable
sealed class SubRouts(val rout:String){
    @Serializable
    object ChatListRout: SubRouts("chat_list_rout")
 @Serializable
    object CallRout: SubRouts("call_list_rout")
 @Serializable
    object ProfileRout: SubRouts("profile_rout")

}
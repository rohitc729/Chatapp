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
}
sealed class MainRouts{
    @Serializable
    object ChatListRout: MainRouts()

}
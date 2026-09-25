package com.rohitchauhan.hiichat.di

import android.content.Context
import com.rohitchauhan.hiichat.data.webrtc.WebRTCClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebRTCModule {

    @Provides
    @Singleton
    fun provideWebRTCClient(@ApplicationContext context: Context): WebRTCClient {
        return WebRTCClient(context)
    }
}

package com.rohitchauhan.hiichat.di

import com.rohitchauhan.hiichat.data.repository.FireBaseRepoImpl
import com.rohitchauhan.hiichat.domain.repository.FirebaseRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    abstract fun provideFirebaseRepo(firebaseRepoImpl: FireBaseRepoImpl): FirebaseRepo
}
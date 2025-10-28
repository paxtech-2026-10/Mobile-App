package com.paxtech.mobileapp.features.authentication.di

import com.paxtech.mobileapp.features.authentication.data.repository.AuthRepositoryImpl
import com.paxtech.mobileapp.features.authentication.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {
    @Binds
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
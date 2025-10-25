package com.paxtech.mobileapp.features.clientDashboard.data.di

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.SalonService
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReviewService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Provides
    @Singleton
    fun provideSalonService(retrofit: Retrofit): SalonService{
        return retrofit.create(SalonService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideReviewService(retrofit: Retrofit): ReviewService{
        return retrofit.create(ReviewService::class.java)
    }
}
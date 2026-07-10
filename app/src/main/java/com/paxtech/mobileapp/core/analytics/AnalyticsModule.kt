package com.paxtech.mobileapp.core.analytics

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalyticsService(retrofit: Retrofit): AnalyticsService =
        retrofit.create(AnalyticsService::class.java)
}

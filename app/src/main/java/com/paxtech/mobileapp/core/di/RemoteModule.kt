package com.paxtech.mobileapp.core.di

import com.paxtech.mobileapp.core.network.LoggingInterceptor
import com.paxtech.mobileapp.features.authentication.data.remote.services.AuthService
import com.paxtech.mobileapp.features.reservations.data.remote.services.ReservationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Provides
    @Singleton
    @Named("url")
    fun provideApiBaseUrl(): String{
        return "https://utime-web-service.onrender.com/"
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("url") url: String,
        authInterceptor: com.paxtech.mobileapp.core.network.AuthInterceptor,
        loggingInterceptor: LoggingInterceptor
    ): Retrofit {
        val okHttpClient = okhttp3.OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideReservationService(retrofit: Retrofit): ReservationService {
        return retrofit.create(ReservationService::class.java)
    }
}
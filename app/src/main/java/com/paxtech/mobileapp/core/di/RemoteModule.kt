package com.paxtech.mobileapp.core.di

import com.paxtech.mobileapp.core.network.AuthInterceptor
import com.paxtech.mobileapp.core.network.LoggingInterceptor
import com.paxtech.mobileapp.features.authentication.data.remote.services.AuthService
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
        return "https://paxtech.azurewebsites.net/"
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("url") url: String,
        authInterceptor: com.paxtech.mobileapp.core.network.AuthInterceptor,
        loggingInterceptor: LoggingInterceptor
    ): Retrofit {
        val okHttpClient = okhttp3.OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)  // Increased from default 10s
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)     // Increased from default 10s
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
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
}
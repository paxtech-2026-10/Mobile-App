package com.paxtech.mobileapp.core.di

import com.paxtech.mobileapp.core.geocoding.GeocodingService
import com.paxtech.mobileapp.core.network.LoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeocodingModule {
    
    @Provides
    @Singleton
    @Named("geocoding_url")
    fun provideGeocodingBaseUrl(): String {
        return "https://nominatim.openstreetmap.org/"
    }
    
    @Provides
    @Singleton
    @Named("geocoding_retrofit")
    fun provideGeocodingRetrofit(
        @Named("geocoding_url") baseUrl: String,
        loggingInterceptor: LoggingInterceptor
    ): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            // Nominatim requiere un User-Agent apropiado
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "MobileApp/1.0 (contact@yourapp.com)")
                    .build()
                chain.proceed(request)
            }
            .build()
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideGeocodingService(
        @Named("geocoding_retrofit") retrofit: Retrofit
    ): GeocodingService {
        return retrofit.create(GeocodingService::class.java)
    }
}


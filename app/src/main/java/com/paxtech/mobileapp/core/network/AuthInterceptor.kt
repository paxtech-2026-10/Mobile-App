package com.paxtech.mobileapp.core.network

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

class AuthInterceptor @Inject constructor(
    @Named("auth_prefs") private val authPrefs: SharedPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authPrefs.getString("auth_token", null)

        println("🔍 AuthInterceptor: Reading token from SharedPreferences: $token")

        val request = chain.request().newBuilder().apply {
            token?.let {
                addHeader("Authorization", "Bearer $it")
                println("🔍 AuthInterceptor: Added Authorization header")
            } ?: println("🔍 AuthInterceptor: No token found, skipping Authorization header")
        }.build()

        return chain.proceed(request)
    }
}




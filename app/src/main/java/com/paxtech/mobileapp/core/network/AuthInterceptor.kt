package com.paxtech.mobileapp.core.network

import android.content.SharedPreferences
import kotlinx.coroutines.flow.firstOrNull

import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Evita añadir header a signin/signup si quieres:
        val path = original.url.encodedPath
        if (path.contains("signin") || path.contains("signup")) {
            return chain.proceed(original)
        }

        val token = runBlocking { tokenStorage.tokenFlow.firstOrNull() }
        val req = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else original

        val response = chain.proceed(req)

        // Si el token ya no es válido → opcional: limpiar y forzar logout
        if (response.code == 401) {
            runBlocking { tokenStorage.clear() }
        }
        return response
    }
}




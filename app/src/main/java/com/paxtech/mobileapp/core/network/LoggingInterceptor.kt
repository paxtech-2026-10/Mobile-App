package com.paxtech.mobileapp.core.network

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import javax.inject.Inject

class LoggingInterceptor @Inject constructor() : Interceptor {
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        println("🔍 LoggingInterceptor: ${request.method} ${request.url}")
        println("🔍 LoggingInterceptor: Headers: ${request.headers}")
        
        // Intentar leer el body del request
        if (request.body != null) {
            try {
                val buffer = Buffer()
                request.body!!.writeTo(buffer)
                println("🔍 LoggingInterceptor: Request body: ${buffer.readUtf8()}")
            } catch (e: Exception) {
                println("🔍 LoggingInterceptor: Could not read request body: ${e.message}")
            }
        }
        
        val response = chain.proceed(request)
        
        println("🔍 LoggingInterceptor: Response code: ${response.code}")
        
        val responseBody = response.peekBody(2048)
        println("🔍 LoggingInterceptor: Response body: ${responseBody.string()}")
        
        return response
    }
}

package com.paxtech.mobileapp.features.payment.data.remote.services

import com.paxtech.mobileapp.features.payment.data.remote.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentService {
    @POST("api/v1/payments")
    suspend fun createPayment(@Body request: CreatePaymentRequestDto): Response<PaymentDto>
    
    @POST("api/v1/payments/create-payment-link")
    suspend fun createPaymentLink(@Body request: CreatePaymentLinkRequestDto): Response<PaymentLinkResponseDto>
    
    @GET("api/v1/payments/{paymentId}")
    suspend fun getPaymentById(@Path("paymentId") paymentId: Long): Response<PaymentDto>

    // Simula un pago exitoso sin depender del webhook de Stripe.
    // Solo funciona si el backend tiene PAYMENTS_SIMULATION_ENABLED=true.
    @POST("api/v1/payments/{paymentId}/confirm")
    suspend fun confirmPayment(@Path("paymentId") paymentId: Long): Response<PaymentDto>
}



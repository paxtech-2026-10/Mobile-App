package com.paxtech.mobileapp.features.payment.domain.repository

import com.paxtech.mobileapp.features.payment.domain.models.Payment

interface PaymentRepository {
    suspend fun createPayment(amount: Double, currency: String, reservationId: Long, clientId: Long): Result<Payment>
    suspend fun createPaymentLink(paymentId: Long, amount: Double, currency: String, description: String): Result<String>
    suspend fun getPaymentById(paymentId: Long): Result<Payment>
}


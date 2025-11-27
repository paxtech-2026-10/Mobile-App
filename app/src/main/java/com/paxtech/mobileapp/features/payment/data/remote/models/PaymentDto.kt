package com.paxtech.mobileapp.features.payment.data.remote.models

data class PaymentDto(
    val id: Long,
    val amount: Double,
    val currency: String,
    val paymentStatus: String, // "PENDING", "SUCCEEDED", "FAILED"
    val stripePaymentLinkId: String?,
    val stripeCheckoutSessionId: String?,
    val reservationId: Long,
    val clientId: Long
)



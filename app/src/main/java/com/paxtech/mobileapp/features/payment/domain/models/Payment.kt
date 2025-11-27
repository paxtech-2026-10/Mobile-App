package com.paxtech.mobileapp.features.payment.domain.models

data class Payment(
    val id: Long,
    val amount: Double,
    val currency: String,
    val paymentStatus: PaymentStatus,
    val stripePaymentLinkId: String?,
    val stripeCheckoutSessionId: String?,
    val reservationId: Long,
    val clientId: Long
)

enum class PaymentStatus {
    PENDING,
    SUCCEEDED,
    FAILED
}



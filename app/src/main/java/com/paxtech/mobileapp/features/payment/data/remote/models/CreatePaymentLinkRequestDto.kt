package com.paxtech.mobileapp.features.payment.data.remote.models

data class CreatePaymentLinkRequestDto(
    val paymentId: Long,
    val amount: Double,
    val currency: String,
    val description: String
)



package com.paxtech.mobileapp.features.payment.data.remote.models

data class CreatePaymentRequestDto(
    val amount: Double,
    val currency: String,
    val reservationId: Long,
    val clientId: Long
)



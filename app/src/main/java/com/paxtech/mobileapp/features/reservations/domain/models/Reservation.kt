package com.paxtech.mobileapp.features.reservations.domain.models

data class Reservation(
    val id: Int,
    val clientId: Int,
    val providerId: Int,
    val paymentId: Int,
    val timeSlotId: Int,
    val workerId: Int
)

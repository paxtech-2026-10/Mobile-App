package com.paxtech.mobileapp.features.reservations.data.remote.models

data class ReservationDto(
    val id: Int,
    val clientId: Int,
    val paymentId: Int,
    val providerId: Int,
    val timeSlotId: Int,
    val workerId: Int
)
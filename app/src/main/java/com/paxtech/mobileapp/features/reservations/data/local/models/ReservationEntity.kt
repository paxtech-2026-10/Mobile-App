package com.paxtech.mobileapp.features.reservations.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Reservation")
data class ReservationEntity(
    @PrimaryKey
    val id: Int,
    val clientId: Int,
    val paymentId: Int,
    val providerId: Int,
    val timeSlotId: Int,
    val workerId: Int
)

package com.paxtech.mobileapp.features.reservations.domain.repository

import com.paxtech.mobileapp.features.reservations.domain.models.Reservation

interface ReservationRepository {
    suspend fun createReservation(reservation: Reservation)
    suspend fun showReservations(): List<Reservation>
}
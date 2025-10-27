package com.paxtech.mobileapp.features.reservations.data.remote.services

import com.paxtech.mobileapp.features.reservations.data.local.models.ReservationEntity
import retrofit2.Response
import retrofit2.http.GET
import com.paxtech.mobileapp.features.reservations.data.remote.models.ReservationDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ReservationService {
    @GET("/api/v1/reservationsDetails")
    suspend fun showReservations(): Response<List<ReservationDto>>

    @POST("/api/v1/reservationsDetails")
    suspend fun createReservations(@Body reservation: ReservationEntity): Response<ReservationDto>
}

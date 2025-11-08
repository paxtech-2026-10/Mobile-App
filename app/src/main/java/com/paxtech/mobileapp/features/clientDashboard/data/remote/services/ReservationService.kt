package com.paxtech.mobileapp.features.clientDashboard.data.remote.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class CreateReservationRequest(
    val clientId: Long,
    val providerId: Long,
    val paymentId: Long,
    val timeSlotId: Long,
    val workerId: Long
)

data class ProviderDto(
    val id: Long,
    val name: String,
    val companyName: String
)

data class PaymentDto(
    val id: Long,
    val amount: Double,
    val currency: String,
    val status: Boolean
)

data class ReservationWorkerDto(
    val id: Long,
    val name: String,
    val specialization: String
)

data class ReservationDetailsDto(
    val id: Long,
    val clientId: Long,
    val provider: ProviderDto,
    val paymentId: PaymentDto,
    val timeSlot: TimeSlotDto,
    val workerId: ReservationWorkerDto
)

interface ReservationService {
    @GET("api/v1/reservationsDetails/details")
    suspend fun getAllReservationsDetails(): Response<List<ReservationDetailsDto>>

    @POST("api/v1/reservationsDetails")
    suspend fun createReservation(@Body body: CreateReservationRequest): Response<Unit>
}




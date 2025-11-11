package com.paxtech.mobileapp.features.clientDashboard.data.remote.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

data class ReservationServiceDto(
    val id: Long,
    val name: String,
    val duration: Int,
    val price: Int,
    val providerId: Int
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
    val serviceId: ReservationServiceDto,
    val timeSlot: TimeSlotDto,
    val workerId: ReservationWorkerDto
)

interface ReservationService {
    @GET("api/v1/reservationsDetails/details")
    suspend fun getAllReservationsDetails(): Response<List<ReservationDetailsDto>>

    @POST("api/v1/reservationsDetails")
    suspend fun createReservation(@Body body: CreateReservationRequest): Response<Unit>
    
    @DELETE("api/v1/reservationsDetails/{reservationId}")
    suspend fun cancelReservation(@Path("reservationId") reservationId: Long): Response<Unit>
}




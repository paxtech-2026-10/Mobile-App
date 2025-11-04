package com.paxtech.mobileapp.features.clientDashboard.data.repositories

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateReservationRequest
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReservationService
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReservationDetailsDto
import javax.inject.Inject

interface ReservationRepository {
    suspend fun getAllDetails(): Result<List<ReservationDetailsDto>>
    suspend fun createReservation(body: CreateReservationRequest): Result<Unit>
}

class ReservationRepositoryImpl @Inject constructor(
    private val reservationService: ReservationService
) : ReservationRepository {
    override suspend fun getAllDetails(): Result<List<ReservationDetailsDto>> = try {
        val response = reservationService.getAllReservationsDetails()
        if (response.isSuccessful) Result.success(response.body().orEmpty())
        else Result.failure(IllegalStateException("HTTP ${'$'}{response.code()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun createReservation(body: CreateReservationRequest): Result<Unit> = try {
        val response = reservationService.createReservation(body)
        if (response.isSuccessful) Result.success(Unit) else Result.failure(IllegalStateException("HTTP ${'$'}{response.code()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}




package com.paxtech.mobileapp.features.clientDashboard.data.repository

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateReservationRequest
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReservationService
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReservationDetailsDto
import javax.inject.Inject

interface ReservationRepository {
    suspend fun getAllDetails(): Result<List<ReservationDetailsDto>>
    suspend fun createReservation(body: CreateReservationRequest): Result<Unit>
    suspend fun cancelReservation(reservationId: Long): Result<Unit>
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
        println("🔍 ReservationRepositoryImpl: Creando reserva con body: $body")
        val response = reservationService.createReservation(body)
        println("🔍 ReservationRepositoryImpl: Respuesta del servidor - código: ${response.code()}, exitoso: ${response.isSuccessful}")
        if (response.isSuccessful) {
            println("🔍 ReservationRepositoryImpl: Reserva creada exitosamente")
            Result.success(Unit)
        } else {
            val errorBody = response.errorBody()?.string()
            println("🔍 ReservationRepositoryImpl: Error al crear reserva - código: ${response.code()}, body: $errorBody")
            Result.failure(IllegalStateException("HTTP ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        println("🔍 ReservationRepositoryImpl: Excepción al crear reserva: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }
    
    override suspend fun cancelReservation(reservationId: Long): Result<Unit> = try {
        val response = reservationService.cancelReservation(reservationId)
        if (response.isSuccessful) Result.success(Unit) else Result.failure(IllegalStateException("HTTP ${'$'}{response.code()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}


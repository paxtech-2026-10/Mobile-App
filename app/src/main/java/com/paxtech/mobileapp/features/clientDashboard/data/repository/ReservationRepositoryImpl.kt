package com.paxtech.mobileapp.features.clientDashboard.data.repository

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateReservationRequest
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReservationService
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReservationDetailsDto
import javax.inject.Inject

interface ReservationRepository {
    suspend fun getAllDetails(): Result<List<ReservationDetailsDto>>
    suspend fun getAllDetailsByClientId(clientId: Long): Result<List<ReservationDetailsDto>>
    suspend fun createReservation(body: CreateReservationRequest): Result<Unit>
    suspend fun cancelReservation(reservationId: Long): Result<Unit>
}

class ReservationRepositoryImpl @Inject constructor(
    private val reservationService: ReservationService
) : ReservationRepository {
    
    override suspend fun getAllDetails(): Result<List<ReservationDetailsDto>> = try {
        println("🔍 ReservationRepositoryImpl: getAllDetails() - Obteniendo todas las reservaciones")
        val response = reservationService.getAllReservationsDetails()
        
        if (response.isSuccessful) {
            val allReservations = response.body() ?: emptyList()
            println("🔍 ReservationRepositoryImpl: getAllDetails() - Total de reservaciones: ${allReservations.size}")
            Result.success(allReservations)
        } else {
            val errorMsg = "HTTP ${response.code()}"
            println("❌ ReservationRepositoryImpl: getAllDetails() - Error HTTP ${response.code()}")
            Result.failure(IllegalStateException(errorMsg))
        }
    } catch (e: Exception) {
        println("❌ ReservationRepositoryImpl: getAllDetails() - Excepción: ${e.message}")
        Result.failure(e)
    }
    
    override suspend fun getAllDetailsByClientId(clientId: Long): Result<List<ReservationDetailsDto>> = try {
        println("🔍🔍 ReservationRepositoryImpl: ===== INICIANDO getAllDetails =====")
        println("🔍 ReservationRepositoryImpl: ClientId recibido: $clientId")
        println("🔍 ReservationRepositoryImpl: Llamando a reservationService.getAllReservationsDetails()")
        
        val response = reservationService.getAllReservationsDetails()
        println("🔍 ReservationRepositoryImpl: Respuesta recibida")
        println("   - Código HTTP: ${response.code()}")
        println("   - Es exitosa: ${response.isSuccessful}")
        println("   - Mensaje: ${response.message()}")
        
        if (response.isSuccessful) {
            val allReservations = response.body() ?: emptyList()
            println("🔍 ReservationRepositoryImpl: Total de reservaciones recibidas del API: ${allReservations.size}")
            
            if (allReservations.isNotEmpty()) {
                println("🔍 ReservationRepositoryImpl: Detalles de todas las reservaciones recibidas:")
                allReservations.forEachIndexed { index, dto ->
                    println("   Reservación $index:")
                    println("      - ID: ${dto.id}")
                    println("      - ClientId: ${dto.clientId} (tipo: ${dto.clientId.javaClass.simpleName})")
                    println("      - ClientId buscado: $clientId (tipo: ${clientId.javaClass.simpleName})")
                    println("      - ¿Coincide?: ${dto.clientId == clientId}")
                    println("      - Service: ${dto.serviceId.name}")
                    println("      - Provider: ${dto.provider.companyName}")
                }
            } else {
                println("⚠️ ReservationRepositoryImpl: El API retornó una lista vacía o null")
            }
            
            val filteredReservations = allReservations.filter { detailsDto ->
                val matches = detailsDto.clientId == clientId
                if (!matches) {
                    println("   ⚠️ Reservación con ID ${detailsDto.id} NO coincide (ClientId: ${detailsDto.clientId} != $clientId)")
                }
                matches
            }
            
            println("🔍 ReservationRepositoryImpl: Reservaciones después del filtro: ${filteredReservations.size}")
            if (filteredReservations.isNotEmpty()) {
                filteredReservations.forEachIndexed { index, dto ->
                    println("   ✅ Reservación filtrada $index: ID=${dto.id}, ClientId=${dto.clientId}")
                }
            }
            
            println("🔍🔍 ReservationRepositoryImpl: ===== FIN getAllDetails =====")
            Result.success(filteredReservations)
        } else {
            val errorMsg = "HTTP ${response.code()}"
            println("❌ ReservationRepositoryImpl: Error HTTP ${response.code()}")
            println("   - Mensaje: ${response.message()}")
            try {
                val errorBody = response.errorBody()?.string()
                println("   - Error body: $errorBody")
            } catch (e: Exception) {
                println("   - No se pudo leer el error body: ${e.message}")
            }
            Result.failure(IllegalStateException(errorMsg))
        }
    } catch (e: Exception) {
        println("❌ ReservationRepositoryImpl: Excepción capturada")
        println("   - Tipo: ${e.javaClass.simpleName}")
        println("   - Mensaje: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    override suspend fun createReservation(body: CreateReservationRequest): Result<Unit> = try {
        val response = reservationService.createReservation(body)
        if (response.isSuccessful) Result.success(Unit) else Result.failure(IllegalStateException("HTTP ${'$'}{response.code()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun cancelReservation(reservationId: Long): Result<Unit> = try {
        val response = reservationService.cancelReservation(reservationId)
        if (response.isSuccessful) Result.success(Unit) else Result.failure(IllegalStateException("HTTP ${'$'}{response.code()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}


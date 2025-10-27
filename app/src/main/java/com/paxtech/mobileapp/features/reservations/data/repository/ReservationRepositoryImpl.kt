package com.paxtech.mobileapp.features.reservations.data.repository

import com.paxtech.mobileapp.features.reservations.data.local.dao.ReservationDao
import com.paxtech.mobileapp.features.reservations.data.local.models.ReservationEntity
import com.paxtech.mobileapp.features.reservations.data.remote.services.ReservationService
import com.paxtech.mobileapp.features.reservations.domain.models.Reservation
import com.paxtech.mobileapp.features.reservations.domain.repository.ReservationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val service: ReservationService,
    private val dao: ReservationDao
): ReservationRepository {

    override suspend fun createReservation(reservation: Reservation) {
        try {
            val dto = ReservationEntity(
                id = reservation.id,
                clientId = reservation.clientId,
                paymentId = reservation.paymentId,
                providerId = reservation.providerId,
                timeSlotId = reservation.timeSlotId,
                workerId = reservation.workerId
            )
            val response = service.createReservations(dto)

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val entity = ReservationEntity(
                        id = body.id,
                        clientId = body.clientId,
                        paymentId = body.paymentId,
                        providerId = body.providerId,
                        timeSlotId = body.timeSlotId,
                        workerId = body.workerId
                    )
                    dao.insert(entity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun showReservations(): List<Reservation> = withContext(Dispatchers.IO) {
        val response = service.showReservations()
        if (response.isSuccessful) {
            response.body()?.let { body ->
                return@withContext body.map { dto ->
                    Reservation(
                        id = dto.id,
                        clientId = dto.clientId,
                        paymentId = dto.paymentId,
                        providerId = dto.providerId,
                        timeSlotId = dto.timeSlotId,
                        workerId = dto.workerId
                    )
                }
            }
        }
        return@withContext emptyList()
    }
}
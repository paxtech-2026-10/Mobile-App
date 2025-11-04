package com.paxtech.mobileapp.features.clientDashboard.data.repository

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.TimeSlotDto
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.TimeSlotService
import javax.inject.Inject

data class TimeSlot(
    val id: Long,
    val startTime: String,
    val endTime: String
)

interface TimeSlotRepository {
    suspend fun getAll(): Result<List<TimeSlot>>
}

class TimeSlotRepositoryImpl @Inject constructor(
    private val timeSlotService: TimeSlotService
): TimeSlotRepository {
    override suspend fun getAll(): Result<List<TimeSlot>> = try {
        val response = timeSlotService.getAllTimeSlots()
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty().map { it.toDomain() })
        } else Result.failure(IllegalStateException("HTTP ${'$'}{response.code()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}

private fun TimeSlotDto.toDomain() = TimeSlot(
    id = id,
    startTime = startTime,
    endTime = endTime
)


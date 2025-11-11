package com.paxtech.mobileapp.features.clientDashboard.data.remote.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class TimeSlotDto(
    val id: Long,
    val startTime: String,
    val endTime: String,
    val status: Boolean,
    val type: String
)

data class CreateTimeSlotRequest(
    val startTime: String,
    val endTime: String,
    val status: Boolean,
    val type: String
)

interface TimeSlotService {
    @GET("api/v1/time-slots")
    suspend fun getAllTimeSlots(): Response<List<TimeSlotDto>>

    @POST("api/v1/time-slots")
    suspend fun createTimeSlot(@Body request: CreateTimeSlotRequest): Response<TimeSlotDto>
}




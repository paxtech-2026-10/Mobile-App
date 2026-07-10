package com.paxtech.mobileapp.core.analytics

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/** Cuerpo del evento de analítica que consume el backend (POST /api/v1/analytics/events). */
data class AnalyticsEventDto(
    val eventType: String,
    val actorType: String,
    val actorId: Long?,
    val providerId: Long?,
    val reservationId: Long?,
    val metadata: Map<String, String>?
)

interface AnalyticsService {
    @POST("api/v1/analytics/events")
    suspend fun track(@Body body: AnalyticsEventDto): Response<Unit>
}

package com.paxtech.mobileapp.features.clientDashboard.data.remote.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class WorkerDto(
    val id: Long,
    val name: String,
    val specialization: String?,
    val photoUrl: String?,
    val providerId: Long
)

interface WorkerService {
    @GET("api/v1/workers")
    suspend fun getAllWorkers(): Response<List<WorkerDto>>

    @GET("api/v1/workers/{workerId}")
    suspend fun getWorkerById(@Path("workerId") workerId: Long): Response<WorkerDto>
}




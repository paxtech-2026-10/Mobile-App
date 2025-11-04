package com.paxtech.mobileapp.features.clientDashboard.data.repository

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.WorkerDto
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.WorkerService
import com.paxtech.mobileapp.features.clientDashboard.domain.models.Worker
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.WorkerRepository
import javax.inject.Inject

class WorkerRepositoryImpl @Inject constructor(
    private val workerService: WorkerService
) : WorkerRepository {

    override suspend fun getAllWorkers(): Result<List<Worker>> {
        return try {
            val response = workerService.getAllWorkers()
            if (response.isSuccessful) {
                val body = response.body().orEmpty()
                Result.success(body.map { it.toDomain() })
            } else {
                Result.failure(IllegalStateException("HTTP ${'$'}{response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun WorkerDto.toDomain(): Worker = Worker(
    id = id,
    name = name,
    specialization = specialization,
    photoUrl = photoUrl,
    providerId = providerId
)


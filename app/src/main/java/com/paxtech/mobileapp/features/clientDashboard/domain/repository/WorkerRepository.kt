package com.paxtech.mobileapp.features.clientDashboard.domain.repository

import com.paxtech.mobileapp.features.clientDashboard.domain.models.Worker

interface WorkerRepository {
    suspend fun getAllWorkers(): Result<List<Worker>>
}


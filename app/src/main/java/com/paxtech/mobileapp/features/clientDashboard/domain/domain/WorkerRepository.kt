package com.paxtech.mobileapp.features.clientDashboard.domain.domain

import com.paxtech.mobileapp.features.clientDashboard.domain.Worker

interface WorkerRepository {
    suspend fun getAllWorkers(): Result<List<Worker>>
}




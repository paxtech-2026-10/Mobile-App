package com.paxtech.mobileapp.features.services.domain

import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi

interface ServiceRepository {
    suspend fun searchService(query: String): List<Service>

    suspend fun getServiceByProviderId(query: Int): List<Service>
}
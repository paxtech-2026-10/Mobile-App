package com.paxtech.mobileapp.features.services.domain

interface ProviderRepository {
    suspend fun findProviderById(query: Int): Provider


}
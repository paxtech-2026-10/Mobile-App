package com.paxtech.mobileapp.features.clientDashboard.domain.repository

import com.paxtech.mobileapp.shared.model.Salon

interface LocalSalonRepository {
    suspend fun getAllFavorites(): List<Salon>
    suspend fun getRecentVisits(): List<Salon>
    suspend fun saveSalonToHistory(salon: Salon)
    suspend fun toggleFavorite(salon: Salon): Boolean
    suspend fun isFavorite(salonId: Int): Boolean
}


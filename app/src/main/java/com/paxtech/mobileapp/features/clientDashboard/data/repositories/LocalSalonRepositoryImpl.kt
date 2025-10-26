package com.paxtech.mobileapp.features.clientDashboard.data.repositories

import com.paxtech.mobileapp.features.clientDashboard.data.local.dao.SalonDao
import com.paxtech.mobileapp.features.clientDashboard.data.local.models.SalonEntity
import com.paxtech.mobileapp.features.clientDashboard.domain.domain.LocalSalonRepository
import com.paxtech.mobileapp.shared.model.Salon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalSalonRepositoryImpl @Inject constructor(
    private val salonDao: SalonDao
) : LocalSalonRepository {
    
    override suspend fun getAllFavorites(): List<Salon> = withContext(Dispatchers.IO) {
        salonDao.getAllFavorites().map { entity ->
            Salon(entity.id, entity.companyName, entity.coverImageUrl)
        }
    }
    
    override suspend fun getRecentVisits(): List<Salon> = withContext(Dispatchers.IO) {
        salonDao.getRecentVisits().map { entity ->
            Salon(entity.id, entity.companyName, entity.coverImageUrl)
        }
    }
    
    override suspend fun saveSalonToHistory(salon: Salon) = withContext(Dispatchers.IO) {
        val entity = SalonEntity(
            id = salon.id,
            companyName = salon.companyName,
            coverImageUrl = salon.coverImageUrl,
            isVisited = true,
            timestamp = System.currentTimeMillis()
        )
        salonDao.insertSalon(entity)
    }
    
    override suspend fun toggleFavorite(salon: Salon): Boolean = withContext(Dispatchers.IO) {
        val isCurrentlyFavorite = salonDao.isFavorite(salon.id)
        val newFavoriteStatus = !isCurrentlyFavorite
        
        // Primero insertar el salón si no existe
        val entity = SalonEntity(
            id = salon.id,
            companyName = salon.companyName,
            coverImageUrl = salon.coverImageUrl,
            isFavorite = newFavoriteStatus,
            timestamp = System.currentTimeMillis()
        )
        salonDao.insertSalon(entity)
        
        // Actualizar el estado de favorito
        salonDao.updateFavoriteStatus(salon.id, newFavoriteStatus)
        
        newFavoriteStatus
    }
    
    override suspend fun isFavorite(salonId: Int): Boolean = withContext(Dispatchers.IO) {
        salonDao.isFavorite(salonId)
    }
}

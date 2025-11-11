package com.paxtech.mobileapp.features.clientDashboard.data.repository

import com.paxtech.mobileapp.features.clientDashboard.data.local.dao.SalonDao
import com.paxtech.mobileapp.features.clientDashboard.data.local.models.SalonEntity
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.LocalSalonRepository
import com.paxtech.mobileapp.shared.model.Salon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalSalonRepositoryImpl @Inject constructor(
    private val salonDao: SalonDao
) : LocalSalonRepository {
    
    override suspend fun getAllFavorites(): List<Salon> = withContext(Dispatchers.IO) {
        salonDao.getAllFavorites().map { entity ->
            Salon(entity.id, entity.companyName, entity.coverImageUrl, entity.location, entity.email, entity.socials)
        }
    }
    
    override suspend fun getRecentVisits(): List<Salon> = withContext(Dispatchers.IO) {
        salonDao.getRecentVisits().map { entity ->
            Salon(entity.id, entity.companyName, entity.coverImageUrl, entity.location, entity.email, entity.socials)
        }
    }
    
    override suspend fun saveSalonToHistory(salon: Salon) = withContext(Dispatchers.IO) {
        // Primero verificar si el salón ya existe para preservar su estado de favorito
        val existingEntity = salonDao.getSalonById(salon.id)
        val isFavorite = existingEntity?.isFavorite ?: false
        
        val entity = SalonEntity(
            id = salon.id,
            companyName = salon.companyName,
            coverImageUrl = salon.coverImageUrl,
            isVisited = true,
            isFavorite = isFavorite, // PRESERVAR el estado de favorito existente
            timestamp = System.currentTimeMillis(),
            location = salon.location,
            email = salon.email,
            socials = salon.socials
        )
        salonDao.insertSalon(entity)
    }
    
    override suspend fun toggleFavorite(salon: Salon): Boolean = withContext(Dispatchers.IO) {
        val isCurrentlyFavorite = salonDao.isFavorite(salon.id)
        val newFavoriteStatus = !isCurrentlyFavorite
        
        // Obtener la entidad existente si existe, para preservar otros datos
        val existingEntity = salonDao.getSalonById(salon.id)
        
        val entity = SalonEntity(
            id = salon.id,
            companyName = salon.companyName,
            coverImageUrl = salon.coverImageUrl,
            isFavorite = newFavoriteStatus,
            isVisited = existingEntity?.isVisited ?: false, // Preservar estado de visitado
            timestamp = existingEntity?.timestamp ?: System.currentTimeMillis(), // Preservar timestamp
            location = salon.location,
            email = salon.email,
            socials = salon.socials
        )
        salonDao.insertSalon(entity)
        
        // Actualizar el estado de favorito (por si acaso)
        salonDao.updateFavoriteStatus(salon.id, newFavoriteStatus)
        
        newFavoriteStatus
    }
    
    override suspend fun isFavorite(salonId: Int): Boolean = withContext(Dispatchers.IO) {
        salonDao.isFavorite(salonId)
    }
}


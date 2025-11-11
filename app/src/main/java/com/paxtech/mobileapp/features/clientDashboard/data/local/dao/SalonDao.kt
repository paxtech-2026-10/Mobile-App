package com.paxtech.mobileapp.features.clientDashboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.paxtech.mobileapp.features.clientDashboard.data.local.models.SalonEntity

@Dao
interface SalonDao {
    
    // Insertar o actualizar salón
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalon(salon: SalonEntity)
    
    // Obtener todos los favoritos
    @Query("SELECT * FROM salons WHERE isFavorite = 1 ORDER BY timestamp DESC")
    suspend fun getAllFavorites(): List<SalonEntity>
    
    // Obtener historial (últimos 5 accedidos)
    @Query("SELECT * FROM salons WHERE isVisited = 1 ORDER BY timestamp DESC LIMIT 5")
    suspend fun getRecentVisits(): List<SalonEntity>
    
    // Obtener un salón por ID
    @Query("SELECT * FROM salons WHERE id = :salonId")
    suspend fun getSalonById(salonId: Int): SalonEntity?
    
    // Verificar si un salón es favorito (Room maneja automáticamente booleans)
    @Query("SELECT EXISTS(SELECT 1 FROM salons WHERE id = :salonId AND isFavorite = 1)")
    suspend fun isFavorite(salonId: Int): Boolean
    
    // Toggle favorito
    @Query("UPDATE salons SET isFavorite = :isFavorite WHERE id = :salonId")
    suspend fun updateFavoriteStatus(salonId: Int, isFavorite: Boolean)
    
    // Marcar como visitado
    @Query("UPDATE salons SET isVisited = 1, timestamp = :timestamp WHERE id = :salonId")
    suspend fun markAsVisited(salonId: Int, timestamp: Long)
    
    // Eliminar salón
    @Query("DELETE FROM salons WHERE id = :salonId")
    suspend fun deleteSalon(salonId: Int)
}

package com.paxtech.mobileapp.features.reservations.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.paxtech.mobileapp.features.reservations.data.local.models.ReservationEntity

@Dao
interface ReservationDao {
    @Insert
    suspend fun insert(vararg entity: ReservationEntity)
}
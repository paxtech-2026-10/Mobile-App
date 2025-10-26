package com.paxtech.mobileapp.features.clientDashboard.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paxtech.mobileapp.features.clientDashboard.data.local.dao.SalonDao
import com.paxtech.mobileapp.features.clientDashboard.data.local.models.SalonEntity

@Database(
    entities = [SalonEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SalonDatabase : RoomDatabase() {
    abstract fun salonDao(): SalonDao
}

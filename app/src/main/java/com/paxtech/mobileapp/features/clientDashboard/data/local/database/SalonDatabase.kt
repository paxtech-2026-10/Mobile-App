package com.paxtech.mobileapp.features.clientDashboard.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paxtech.mobileapp.features.clientDashboard.data.local.converters.ListStringConverter
import com.paxtech.mobileapp.features.clientDashboard.data.local.dao.SalonDao
import com.paxtech.mobileapp.features.clientDashboard.data.local.models.SalonEntity

@Database(
    entities = [SalonEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(ListStringConverter::class)
abstract class SalonDatabase : RoomDatabase() {
    abstract fun salonDao(): SalonDao
}

package com.paxtech.mobileapp.features.reservations.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paxtech.mobileapp.features.reservations.data.local.dao.ReservationDao
import com.paxtech.mobileapp.features.reservations.data.local.models.ReservationEntity

@Database(entities = [ReservationEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun reservationDao(): ReservationDao
}
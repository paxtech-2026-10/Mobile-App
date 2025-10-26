package com.paxtech.mobileapp.features.clientDashboard.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salons")
data class SalonEntity(
    @PrimaryKey val id: Int,
    val companyName: String,
    val coverImageUrl: String,
    val isFavorite: Boolean = false,
    val isVisited: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

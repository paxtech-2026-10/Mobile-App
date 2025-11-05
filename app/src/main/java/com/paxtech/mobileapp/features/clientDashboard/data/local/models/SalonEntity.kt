package com.paxtech.mobileapp.features.clientDashboard.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.paxtech.mobileapp.features.clientDashboard.data.local.converters.ListStringConverter

@Entity(tableName = "salons")
@TypeConverters(ListStringConverter::class)
data class SalonEntity(
    @PrimaryKey val id: Int,
    val companyName: String,
    val coverImageUrl: String,
    val isFavorite: Boolean = false,
    val isVisited: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val location: String,
    val email: String,
    val socials: List<String>
)

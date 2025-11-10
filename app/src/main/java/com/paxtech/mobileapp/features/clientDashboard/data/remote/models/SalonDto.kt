package com.paxtech.mobileapp.features.clientDashboard.data.remote.models

data class SalonDto (
    val id: Int?,
    val providerId: Int?,
    val companyName: String?,
    val location: String?,
    val email: String?,
    val profileImageUrl: String?,
    val coverImageUrl: String?,
    val socials: Map<String, String>? = null
)


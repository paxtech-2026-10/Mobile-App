package com.paxtech.mobileapp.features.clientDashboard.data.remote.model

data class ReviewDto(
    val id: Int,
    val clientId: Int,
    val providerId: Int,
    val rating: Int,
    val review: String
)

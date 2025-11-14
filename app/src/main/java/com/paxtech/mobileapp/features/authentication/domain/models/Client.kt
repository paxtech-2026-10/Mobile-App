package com.paxtech.mobileapp.features.authentication.domain.models

data class Client(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val userId: Int,
    val profileImageUrl: String? = null
)
package com.paxtech.mobileapp.features.authentication.domain.models

data class User(
    val id: Int,
    val email: String,
    val token: String? = null
)
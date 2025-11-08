package com.paxtech.mobileapp.features.authentication.data.remote.models

data class SignInResponseDto(
    val id: Int,
    val email: String,
    val token: String
)
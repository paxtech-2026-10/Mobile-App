package com.paxtech.mobileapp.features.authentication.data.remote.models

data class CreateClientRequestDto(
    val firstName: String,
    val lastName: String,
    val userId: Int
)
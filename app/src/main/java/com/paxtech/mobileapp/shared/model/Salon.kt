package com.paxtech.mobileapp.shared.model

import retrofit2.http.Url

data class Salon(
    val id: Int,
    val companyName: String,
    val coverImageUrl: String,
    val location: String,
    val email: String,
    val socials: List<String>
)

package com.paxtech.mobileapp.features.clientDashboard.domain

data class Worker(
    val id: Long,
    val name: String,
    val specialization: String?,
    val photoUrl: String?,
    val providerId: Long
)




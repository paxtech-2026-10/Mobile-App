package com.paxtech.mobileapp.features.clientDashboard.data.remote.models

data class DiscountDto(
    val id: Int?,
    val title: String?,
    val subtitle: String?,
    val content: String?,
    val discountType: String?, // "PERCENTAGE" o "FIXED"
    val discountValue: Int?,
    val providerProfileId: Int?
)


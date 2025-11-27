package com.paxtech.mobileapp.features.clientDashboard.domain.model

data class Discount(
    val id: Int,
    val title: String,
    val subtitle: String,
    val content: String,
    val discountType: DiscountType,
    val discountValue: Int,
    val providerProfileId: Int
)

enum class DiscountType {
    PERCENTAGE,
    FIXED;
    
    companion object {
        fun fromString(value: String?): DiscountType {
            return when(value?.uppercase()) {
                "PERCENTAGE" -> PERCENTAGE
                "FIXED" -> FIXED
                else -> PERCENTAGE
            }
        }
    }
}


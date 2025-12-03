package com.paxtech.mobileapp.features.clientDashboard.presentation.shared

data class ReservationData(
    val salonId: Int,
    val salonName: String,
    val salonAddress: String,
    val salonRating: Double,
    val salonImageUrl: String,
    val service: ServiceData,
    val selectedProfessional: String = "",
    val selectedProfessionalId: Long = 0L,
    val clientId: Long = 0L,
    val providerId: Long = 0L,
    val selectedDate: String = "",
    val selectedTime: String = "",
    val formattedDate: String = "",
    val formattedTime: String = "",
    val timeSlotId: Long = 0L,
    // Información del descuento aplicado
    val appliedDiscountTitle: String? = null,
    val discountAmount: Double = 0.0,
    val discountType: String? = null  // "PERCENTAGE" o "FIXED"
)

data class ServiceData(
    val id: String,
    val title: String,
    val subtitle: String,
    val price: String,
    val durationMins: Int
)


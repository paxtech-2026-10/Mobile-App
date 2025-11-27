package com.paxtech.mobileapp.features.clientDashboard.domain.repository

import com.paxtech.mobileapp.features.clientDashboard.domain.model.Discount

interface DiscountRepository {
    suspend fun getAllDiscounts(): List<Discount>
}


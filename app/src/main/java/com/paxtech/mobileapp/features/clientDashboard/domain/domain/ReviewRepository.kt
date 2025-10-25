package com.paxtech.mobileapp.features.clientDashboard.domain.domain

import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ReviewUi

interface ReviewRepository {
    suspend fun getReviewsByProviderId(providerId: Int): List<ReviewUi>
}

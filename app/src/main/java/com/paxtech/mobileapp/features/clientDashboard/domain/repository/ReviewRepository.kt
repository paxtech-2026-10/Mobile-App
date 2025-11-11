package com.paxtech.mobileapp.features.clientDashboard.domain.repository

import com.paxtech.mobileapp.features.clientDashboard.domain.model.RatingSummary
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ReviewUi

interface ReviewRepository {
    suspend fun getReviewsByProviderId(providerId: Int): List<ReviewUi>
    suspend fun getRatingSummary(providerId: Int): RatingSummary?
}


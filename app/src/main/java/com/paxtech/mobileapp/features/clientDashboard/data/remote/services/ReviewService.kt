package com.paxtech.mobileapp.features.clientDashboard.data.remote.services

import com.paxtech.mobileapp.features.clientDashboard.data.remote.models.ReviewDto
import retrofit2.Response
import retrofit2.http.GET

interface ReviewService {
    @GET("api/v1/reviews")
    suspend fun getAllReviews(): Response<List<ReviewDto>>
}


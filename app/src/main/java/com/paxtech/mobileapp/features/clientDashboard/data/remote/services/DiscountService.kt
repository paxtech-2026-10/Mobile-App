package com.paxtech.mobileapp.features.clientDashboard.data.remote.services

import com.paxtech.mobileapp.features.clientDashboard.data.remote.models.DiscountDto
import retrofit2.Response
import retrofit2.http.GET

interface DiscountService {
    @GET("api/v1/discounts")
    suspend fun getAllDiscounts(): Response<List<DiscountDto>>
}


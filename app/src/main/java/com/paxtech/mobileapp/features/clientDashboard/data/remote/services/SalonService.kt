package com.paxtech.mobileapp.features.clientDashboard.data.remote.services

import com.paxtech.mobileapp.features.clientDashboard.data.remote.models.SalonDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SalonService {
    @GET("api/v1/provider-profiles")
    suspend fun getAllSalons(): Response<List<SalonDto>?>

    @GET("api/v1/provider-profiles/{id}")
    suspend fun getSalonById(@Path("id") id: Int): Response<SalonDto>

    @GET("api/v1/provider-profiles/search")
    suspend fun getSalonByName(@Query("companyName") companyName: String): Response<List<SalonDto>?>
}



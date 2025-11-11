package com.paxtech.mobileapp.features.authentication.data.remote.services

import com.paxtech.mobileapp.features.authentication.data.remote.models.ClientDto
import com.paxtech.mobileapp.features.authentication.data.remote.models.CreateClientRequestDto
import com.paxtech.mobileapp.features.authentication.data.remote.models.SignInRequestDto
import com.paxtech.mobileapp.features.authentication.data.remote.models.SignInResponseDto
import com.paxtech.mobileapp.features.authentication.data.remote.models.SignUpRequestDto
import com.paxtech.mobileapp.features.authentication.data.remote.models.SignUpResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.Response

interface AuthService {
    @POST("api/v1/authentication/sign-up")
    suspend fun signUp(@Body request: SignUpRequestDto): Response<SignUpResponseDto>

    @POST("api/v1/authentication/sign-in")
    suspend fun signIn(@Body request: SignInRequestDto): Response<SignInResponseDto>

    @POST("api/v1/clients")
    suspend fun createClient(@Body request: CreateClientRequestDto): Response<Unit>
    
    @GET("api/v1/clients")
    suspend fun getAllClients(): Response<List<ClientDto>>
}

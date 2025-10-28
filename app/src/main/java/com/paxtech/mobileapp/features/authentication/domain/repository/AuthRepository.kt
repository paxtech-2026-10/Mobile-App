package com.paxtech.mobileapp.features.authentication.domain.repository

import com.paxtech.mobileapp.features.authentication.domain.models.Client
import com.paxtech.mobileapp.features.authentication.domain.models.User

interface AuthRepository {
    suspend fun signUp(email: String, password: String): User
    suspend fun signIn(email: String, password: String): User
    suspend fun createClient(firstName: String, lastName: String, userId: Int): Client
}
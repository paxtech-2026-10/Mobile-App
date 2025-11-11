package com.paxtech.mobileapp.features.authentication.data.repository

import com.paxtech.mobileapp.core.network.TokenStorage
import com.paxtech.mobileapp.features.authentication.data.remote.models.ClientDto
import com.paxtech.mobileapp.features.authentication.data.remote.models.CreateClientRequestDto
import com.paxtech.mobileapp.features.authentication.data.remote.models.SignInRequestDto
import com.paxtech.mobileapp.features.authentication.data.remote.models.SignUpRequestDto
import com.paxtech.mobileapp.features.authentication.data.remote.models.SignUpResponseDto
import com.paxtech.mobileapp.features.authentication.data.remote.services.AuthService
import com.paxtech.mobileapp.features.authentication.domain.models.Client
import com.paxtech.mobileapp.features.authentication.domain.models.User
import com.paxtech.mobileapp.features.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun signUp(email: String, password: String): User = withContext(Dispatchers.IO) {
        try {
            println("🔍 AuthRepositoryImpl: Making sign-up call...")
            val request = SignUpRequestDto(email, password)
            val resp = authService.signUp(request)

            if (resp.isSuccessful) {
                val body = resp.body()
                println("🔍 AuthRepositoryImpl: Sign-up response: $body")
                body?.let {
                    User(it.id, it.email)
                } ?: throw Exception("Sign-up response body is null")
            } else {
                val errorBody = resp.errorBody()?.string()
                println("🔍 AuthRepositoryImpl: Sign-up failed with code ${resp.code()}, body: $errorBody")
                println("🔍 AuthRepositoryImpl: Response headers: ${resp.headers()}")
                throw Exception("Sign-up failed: ${resp.code()} - $errorBody")
            }
        } catch (e: Exception) {
            println("🔍 AuthRepositoryImpl: Exception in sign-up: ${e.message}")
            throw e
        }
    }

    override suspend fun signIn(email: String, password: String): User = withContext(Dispatchers.IO) {
        try {
            println("🔍 AuthRepositoryImpl: Making sign-in call...")
            val request = SignInRequestDto(email, password)
            val resp = authService.signIn(request)

            if (resp.isSuccessful) {
                val body = resp.body()
                println("🔍 AuthRepositoryImpl: Sign-in response: $body")
                body?.let {
                    // Guardar el token en TokenStorage
                    if (!it.token.isNullOrBlank()) {
                        tokenStorage.save(it.token)
                        println("🔍 AuthRepositoryImpl: Token saved to TokenStorage")
                    }
                    User(it.id, it.email, it.token)
                } ?: throw Exception("Sign-in response body is null")
            } else {
                println("🔍 AuthRepositoryImpl: Sign-in failed: ${resp.errorBody()?.string()}")
                throw Exception("Sign-in failed: ${resp.code()}")
            }
        } catch (e: Exception) {
            println("🔍 AuthRepositoryImpl: Exception in sign-in: ${e.message}")
            throw e
        }
    }

    override suspend fun createClient(firstName: String, lastName: String, userId: Int): Client = withContext(Dispatchers.IO) {
        try {
            println("🔍 AuthRepositoryImpl: Making create client call...")
            val request = CreateClientRequestDto(firstName, lastName, userId)
            println("🔍 AuthRepositoryImpl: Creating client with data - firstName: $firstName, lastName: $lastName, userId: $userId")
            val resp = authService.createClient(request)

            if (resp.isSuccessful) {
                println("🔍 AuthRepositoryImpl: Client created successfully")
                // Después de crear, obtener el cliente para obtener su ID
                val allClientsResp = authService.getAllClients()
                if (allClientsResp.isSuccessful) {
                    val clientDto = allClientsResp.body()?.firstOrNull { it.userId == userId }
                    if (clientDto != null) {
                        println("🔍 AuthRepositoryImpl: Client ID obtenido: ${clientDto.id}")
                        return@withContext Client(clientDto.id, clientDto.firstName, clientDto.lastName, clientDto.userId)
                    }
                }
                // Si no se puede obtener el ID, lanzar error
                throw Exception("Could not retrieve created client ID")
            } else {
                val errorBody = resp.errorBody()?.string()
                println("🔍 AuthRepositoryImpl: Create client failed: $errorBody")
                println("🔍 AuthRepositoryImpl: Response code: ${resp.code()}")
                println("🔍 AuthRepositoryImpl: Response headers: ${resp.headers()}")
                throw Exception("Create client failed: ${resp.code()}")
            }
        } catch (e: Exception) {
            println("🔍 AuthRepositoryImpl: Exception in create client: ${e.message}")
            throw e
        }
    }
    
    override suspend fun getClientByUserId(userId: Int): Client? = withContext(Dispatchers.IO) {
        try {
            println("🔍 AuthRepositoryImpl: Getting client for userId: $userId")
            val resp = authService.getAllClients()
            
            if (resp.isSuccessful) {
                val clients = resp.body()
                val clientDto = clients?.firstOrNull { it.userId == userId }
                if (clientDto != null) {
                    println("🔍 AuthRepositoryImpl: Client found: ${clientDto.firstName} ${clientDto.lastName}, ID: ${clientDto.id}")
                    Client(clientDto.id, clientDto.firstName, clientDto.lastName, clientDto.userId)
                } else {
                    println("🔍 AuthRepositoryImpl: Client not found for userId: $userId")
                    null
                }
            } else {
                println("🔍 AuthRepositoryImpl: Get clients failed: ${resp.code()}")
                null
            }
        } catch (e: Exception) {
            println("🔍 AuthRepositoryImpl: Exception getting client: ${e.message}")
            null
        }
    }
}


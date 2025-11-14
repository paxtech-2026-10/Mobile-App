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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
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
                        return@withContext Client(
                            clientDto.id, 
                            clientDto.firstName, 
                            clientDto.lastName, 
                            clientDto.userId,
                            clientDto.profileImageUrl
                        )
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
                    Client(
                        clientDto.id, 
                        clientDto.firstName, 
                        clientDto.lastName, 
                        clientDto.userId,
                        clientDto.profileImageUrl
                    )
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
    
    override suspend fun uploadClientProfileImage(clientId: Int, imageFile: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            println("🔍 AuthRepositoryImpl: Starting image upload for clientId: $clientId")
            println("🔍 AuthRepositoryImpl: Image file path: ${imageFile.absolutePath}")
            println("🔍 AuthRepositoryImpl: Image file name: ${imageFile.name}")
            println("🔍 AuthRepositoryImpl: Image file exists: ${imageFile.exists()}")
            println("🔍 AuthRepositoryImpl: Image file size: ${imageFile.length()} bytes")
            
            // Detectar el tipo MIME basado en la extensión del archivo
            val mimeType = getImageMimeType(imageFile)
            println("🔍 AuthRepositoryImpl: Detected MIME type: $mimeType")
            
            if (mimeType == null) {
                val errorMsg = "Tipo de archivo no soportado. Solo se permiten: JPEG, PNG, WEBP, GIF"
                println("🔍 AuthRepositoryImpl: $errorMsg")
                return@withContext Result.failure(Exception(errorMsg))
            }
            
            val requestFile = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
            println("🔍 AuthRepositoryImpl: Created request body with MIME type: $mimeType")
            
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
            println("🔍 AuthRepositoryImpl: Created multipart body with field name: file, filename: ${imageFile.name}")
            
            println("🔍 AuthRepositoryImpl: Sending upload request to API...")
            val response = authService.uploadProfileImage(clientId, body)
            
            println("🔍 AuthRepositoryImpl: Upload response code: ${response.code()}")
            println("🔍 AuthRepositoryImpl: Upload response successful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                println("🔍 AuthRepositoryImpl: Image uploaded successfully!")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                println("🔍 AuthRepositoryImpl: Upload profile image failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Upload failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            println("🔍 AuthRepositoryImpl: Exception uploading profile image: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Detecta el tipo MIME de una imagen basándose en su extensión
     */
    private fun getImageMimeType(file: File): String? {
        val extension = file.extension.lowercase()
        println("🔍 AuthRepositoryImpl: File extension: $extension")
        
        return when (extension) {
            "jpg", "jpeg" -> {
                println("🔍 AuthRepositoryImpl: Detected JPEG image")
                "image/jpeg"
            }
            "png" -> {
                println("🔍 AuthRepositoryImpl: Detected PNG image")
                "image/png"
            }
            "webp" -> {
                println("🔍 AuthRepositoryImpl: Detected WEBP image")
                "image/webp"
            }
            "gif" -> {
                println("🔍 AuthRepositoryImpl: Detected GIF image")
                "image/gif"
            }
            else -> {
                println("🔍 AuthRepositoryImpl: Unknown file extension: $extension")
                null
            }
        }
    }
}


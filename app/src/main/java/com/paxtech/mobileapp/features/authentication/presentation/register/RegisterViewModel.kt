package com.paxtech.mobileapp.features.authentication.presentation.register

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.authentication.domain.models.Client
import com.paxtech.mobileapp.features.authentication.domain.models.User
import com.paxtech.mobileapp.features.authentication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @Named("auth_prefs") private val authPrefs: SharedPreferences
) : ViewModel() {
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    
    private val _client = MutableStateFlow<Client?>(null)
    val client: StateFlow<Client?> = _client
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): User? {
        return try {
            _isLoading.value = true
            _error.value = null

            // 1. Registrar el usuario (sin token)
            val signedUpUser = authRepository.signUp(email, password)
            _user.value = signedUpUser

            println("🔍 RegisterViewModel: User signed up successfully: ${signedUpUser.id}")

            // 2. Hacer sign-in para obtener el token
            val signedInUser = authRepository.signIn(email, password)
            _user.value = signedInUser

            println("🔍 RegisterViewModel: User signed in successfully with token: ${signedInUser.token}")

            // Guardar el token en SharedPreferences
            if (signedInUser.token != null) {
                authPrefs.edit().apply {
                    putString("auth_token", signedInUser.token)
                    putInt("user_id", signedInUser.id)
                    putString("user_first_name", firstName)
                    putString("user_last_name", lastName)
                    putString("user_full_name", "$firstName $lastName")
                }.commit()
                println("🔍 RegisterViewModel: Token and user data saved to SharedPreferences")
            }
            
            // 3. Ahora crear el cliente con el token disponible
            val client = authRepository.createClient(firstName, lastName, signedInUser.id)
            _client.value = client

            println("🔍 RegisterViewModel: Client created successfully")

            signedInUser // Retornar el usuario con token

        } catch (e: Exception) {
            _error.value = "Error al registrar: ${e.message}"
            println("🔍 RegisterViewModel: Register error: ${e.message}")
            null
        } finally {
            _isLoading.value = false
        }
    }
}
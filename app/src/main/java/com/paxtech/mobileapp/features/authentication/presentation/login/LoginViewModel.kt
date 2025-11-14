package com.paxtech.mobileapp.features.authentication.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.core.network.TokenStorage
import com.paxtech.mobileapp.features.authentication.domain.models.User
import com.paxtech.mobileapp.features.authentication.domain.repository.AuthRepository
import com.paxtech.mobileapp.features.authentication.domain.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val user = authRepository.signIn(email, password)
                _user.value = user

                // El token ya se guardó en AuthRepositoryImpl
                println("🔍 LoginViewModel: User signed in successfully with token: ${user.token}")

                // Obtener y guardar el nombre del cliente
                if (user.token != null) {
                    try {
                        val client = authRepository.getClientByUserId(user.id)
                        if (client != null) {
                            userDataRepository.saveUserName(client.firstName, client.lastName)
                            userDataRepository.saveClientId(client.id)
                            userDataRepository.saveProfileImageUrl(client.profileImageUrl)
                            println("🔍 LoginViewModel: User name saved: ${client.firstName} ${client.lastName}")
                            println("🔍 LoginViewModel: ClientId guardado: ${client.id}")
                            println("🔍 LoginViewModel: ProfileImageUrl guardado: ${client.profileImageUrl}")
                        } else {
                            println("🔍 LoginViewModel: Client not found for userId: ${user.id}")
                        }
                    } catch (e: Exception) {
                        println("🔍 LoginViewModel: Could not get client info: ${e.message}")
                        // No fallar el login si no se puede obtener el nombre
                    }
                }

            } catch (e: Exception) {
                _error.value = "Error al iniciar sesión: ${e.message}"
                println("🔍 LoginViewModel: Sign-in error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
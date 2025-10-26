package com.paxtech.mobileapp.features.authentication.presentation.login

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.authentication.domain.models.User
import com.paxtech.mobileapp.features.authentication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @Named("auth_prefs") private val authPrefs: SharedPreferences
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

                // Guardar el token y datos del usuario en SharedPreferences
                if (user.token != null) {
                    val currentName = authPrefs.getString("user_full_name", null)
                    val nameToUse = currentName ?: email.split("@")[0] // Usar el email si no hay nombre guardado
                    
                    authPrefs.edit().apply {
                        putString("auth_token", user.token)
                        putInt("user_id", user.id)
                        putString("user_email", user.email)
                        putString("user_full_name", nameToUse)
                    }.apply()
                    println("🔍 LoginViewModel: Token and user data saved to SharedPreferences")
                }
                println("🔍 LoginViewModel: User signed in successfully with token: ${user.token}")

            } catch (e: Exception) {
                _error.value = "Error al iniciar sesión: ${e.message}"
                println("🔍 LoginViewModel: Sign-in error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
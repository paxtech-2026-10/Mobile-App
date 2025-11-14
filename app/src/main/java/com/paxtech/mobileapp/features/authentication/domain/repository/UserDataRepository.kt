package com.paxtech.mobileapp.features.authentication.domain.repository

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Named

class UserDataRepository @Inject constructor(
    @Named("auth_prefs") private val authPrefs: SharedPreferences
) {
    // Flow para observar cambios en la URL de la imagen de perfil
    private val _profileImageUrlFlow = MutableStateFlow<String?>(null)
    val profileImageUrlFlow: StateFlow<String?> = _profileImageUrlFlow.asStateFlow()
    fun saveUserName(firstName: String, lastName: String) {
        authPrefs.edit().apply {
            putString("user_first_name", firstName)
            putString("user_last_name", lastName)
            putString("user_full_name", "$firstName $lastName")
        }.apply()
    }
    
    fun getUserName(): String {
        return authPrefs.getString("user_full_name", "Usuario") ?: "Usuario"
    }
    
    fun getUserId(): Int {
        return authPrefs.getInt("user_id", 0)
    }
    
    fun saveClientId(clientId: Int) {
        authPrefs.edit().apply {
            putInt("client_id", clientId)
        }.apply()
        println("🔍 UserDataRepository: ClientId guardado: $clientId")
    }
    
    fun getClientId(): Int {
        val clientId = authPrefs.getInt("client_id", 0)
        println("🔍 UserDataRepository: ClientId obtenido: $clientId")
        return clientId
    }
    
    fun saveProfileImageUrl(url: String?) {
        println("🔍 UserDataRepository: saveProfileImageUrl() llamado con URL: $url")
        val previousValue = _profileImageUrlFlow.value
        println("🔍 UserDataRepository: Valor anterior en Flow: $previousValue")
        
        authPrefs.edit().apply {
            if (url != null) {
                putString("user_avatar_url", url)
            } else {
                remove("user_avatar_url")
            }
        }.apply()
        
        // Emitir el cambio al Flow para que los observadores se actualicen
        _profileImageUrlFlow.value = url
        println("🔍 UserDataRepository: ProfileImageUrl guardado en SharedPreferences y Flow actualizado")
        println("🔍 UserDataRepository: Nuevo valor en Flow: ${_profileImageUrlFlow.value}")
    }
    
    fun getProfileImageUrl(): String? {
        val url = authPrefs.getString("user_avatar_url", null)
        println("🔍 UserDataRepository: getProfileImageUrl() llamado, URL desde SharedPreferences: $url")
        println("🔍 UserDataRepository: Valor actual en Flow antes de sincronizar: ${_profileImageUrlFlow.value}")
        
        // Inicializar el Flow con el valor actual (sincronizar siempre)
        _profileImageUrlFlow.value = url
        println("🔍 UserDataRepository: Flow sincronizado con valor: ${_profileImageUrlFlow.value}")
        return url
    }
}

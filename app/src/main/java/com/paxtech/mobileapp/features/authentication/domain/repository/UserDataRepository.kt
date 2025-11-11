package com.paxtech.mobileapp.features.authentication.domain.repository

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Named

class UserDataRepository @Inject constructor(
    @Named("auth_prefs") private val authPrefs: SharedPreferences
) {
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
}

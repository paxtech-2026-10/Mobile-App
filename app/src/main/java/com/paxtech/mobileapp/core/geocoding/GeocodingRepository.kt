package com.paxtech.mobileapp.core.geocoding

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodingRepository @Inject constructor(
    private val geocodingService: GeocodingService
) {
    /**
     * Convierte coordenadas en una dirección legible
     * @param latitude Latitud
     * @param longitude Longitud
     * @return Dirección formateada o null si hay error
     */
    suspend fun reverseGeocode(latitude: Double, longitude: Double): String? {
        return try {
            // Nominatim tiene límite de 1 solicitud por segundo
            delay(1000)
            
            val response = geocodingService.reverseGeocode(latitude, longitude)
            
            if (response.isSuccessful) {
                val body = response.body()
                body?.displayName ?: formatAddress(body?.address)
            } else {
                println("🔍 GeocodingRepository: Error response: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            println("🔍 GeocodingRepository: Exception: ${e.message}")
            null
        }
    }
    
    private fun formatAddress(address: Address?): String? {
        if (address == null) return null
        
        val parts = mutableListOf<String>()
        
        // Construir dirección en orden: calle, número, ciudad, estado, país
        if (!address.road.isNullOrBlank()) {
            val street = if (!address.houseNumber.isNullOrBlank()) {
                "${address.road} ${address.houseNumber}"
            } else {
                address.road
            }
            parts.add(street)
        }
        
        val city = address.city ?: address.town ?: address.village
        if (!city.isNullOrBlank()) {
            parts.add(city)
        }
        
        if (!address.state.isNullOrBlank()) {
            parts.add(address.state)
        }
        
        if (!address.country.isNullOrBlank()) {
            parts.add(address.country)
        }
        
        return if (parts.isNotEmpty()) {
            parts.joinToString(", ")
        } else {
            null
        }
    }
}


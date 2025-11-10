package com.paxtech.mobileapp.core.utils

import android.location.Location

object LocationUtils {
    /**
     * Parsea un string de ubicación y extrae latitud y longitud
     * Formatos soportados:
     * - "lat,lng" -> "-12.0464,-77.0428"
     * - "address|lat,lng" -> "Av. Principal|-12.0464,-77.0428"
     * - "lat,lng|address" -> "-12.0464,-77.0428|Av. Principal"
     */
    fun parseCoordinates(locationString: String): Pair<Double, Double>? {
        return try {
            // Regex que permite espacios opcionales alrededor de la coma
            // Captura números con o sin decimales: -12.123 o -12
            val regex = Regex("""(-?\d+(?:\.\d+)?)\s*,\s*(-?\d+(?:\.\d+)?)""")
            val match = regex.find(locationString)
            
            if (match != null) {
                val lat = match.groupValues[1].toDouble()
                val lng = match.groupValues[2].toDouble()
                println("🔍 LocationUtils: Parsed coordinates from '$locationString': lat=$lat, lng=$lng")
                // Validar que las coordenadas estén en rangos válidos
                if (lat in -90.0..90.0 && lng in -180.0..180.0) {
                    Pair(lat, lng)
                } else {
                    println("🔍 LocationUtils: Coordinates out of range: lat=$lat, lng=$lng")
                    null
                }
            } else {
                println("🔍 LocationUtils: No coordinates found in: $locationString")
                null
            }
        } catch (e: Exception) {
            println("🔍 LocationUtils: Error parsing coordinates from: $locationString - ${e.message}")
            null
        }
    }
    
    /**
     * Extrae solo la dirección del string (si existe)
     */
    fun extractAddress(locationString: String): String {
        val coordinates = parseCoordinates(locationString)
        if (coordinates == null) {
            return locationString
        }
        
        // Regex que permite espacios opcionales alrededor de la coma
        val regex = Regex("""(-?\d+(?:\.\d+)?)\s*,\s*(-?\d+(?:\.\d+)?)""")
        return regex.replace(locationString, "")
            .replace("|", "")
            .trim()
            .ifEmpty { locationString }
    }
    
    /**
     * Calcula la distancia en kilómetros entre dos coordenadas
     */
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        val distanceKm = results[0] / 1000f // Convertir metros a kilómetros
        println("🔍 LocationUtils: Distance between ($lat1, $lon1) and ($lat2, $lon2) = ${distanceKm}km")
        return distanceKm
    }
    
    /**
     * Formatea la distancia para mostrar
     */
    fun formatDistance(distanceKm: Float): String {
        return when {
            distanceKm < 0.1f -> "< 100 m"
            distanceKm < 1f -> "%.0f m".format(distanceKm * 1000)
            else -> "%.1f km".format(distanceKm)
        }
    }
}


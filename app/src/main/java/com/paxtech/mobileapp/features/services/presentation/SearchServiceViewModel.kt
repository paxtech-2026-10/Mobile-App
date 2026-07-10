package com.paxtech.mobileapp.features.services.presentation

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.core.location.LocationManager
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.SalonRepository
import com.paxtech.mobileapp.features.services.domain.Service
import com.paxtech.mobileapp.features.services.domain.ServiceRepository
import com.paxtech.mobileapp.shared.model.Salon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

/** Resultado de búsqueda: un servicio junto con el salón que lo ofrece y la distancia al usuario. */
data class ServiceSearchResult(
    val service: Service,
    val salonName: String,
    val salonLocation: String,
    val distanceText: String? = null
)

@HiltViewModel
class SearchServiceViewModel @Inject constructor(
    private val repository: ServiceRepository,
    private val salonRepository: SalonRepository,
    private val locationManager: LocationManager
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<ServiceSearchResult>>(emptyList())
    val results: StateFlow<List<ServiceSearchResult>> = _results

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched

    // Cache de salones por providerId para no repetir llamadas entre búsquedas.
    private val salonCache = mutableMapOf<Int, Salon?>()

    fun onChangeQuery(value: String) {
        _query.value = value
    }

    fun searchService() {
        viewModelScope.launch {
            _isLoading.value = true
            _hasSearched.value = true

            val services = repository.searchService(_query.value)

            // Traer la info del salón (una llamada por salón distinto, con cache).
            val missing = services.map { it.providerId }.distinct().filter { it !in salonCache }
            if (missing.isNotEmpty()) {
                val fetched = coroutineScope {
                    missing.map { pid -> async { pid to salonRepository.getSalonById(pid) } }.awaitAll()
                }
                fetched.forEach { (pid, salon) -> salonCache[pid] = salon }
            }

            // Ubicación del usuario para calcular distancias (null si no hay permiso/ubicación).
            val userLocation = locationManager.getCurrentLocation()

            _results.value = services.map { service ->
                val salon = salonCache[service.providerId]
                val rawLocation = salon?.location.orEmpty()
                ServiceSearchResult(
                    service = service,
                    salonName = salon?.companyName?.takeIf { it.isNotBlank() } ?: "Salón",
                    salonLocation = parseAddress(rawLocation),
                    distanceText = distanceTextTo(userLocation, parseLatLng(rawLocation))
                )
            }

            _isLoading.value = false
        }
    }

    /** El backend guarda la ubicación como "Dirección|lat,lng"; nos quedamos solo con la dirección. */
    private fun parseAddress(location: String): String =
        location.substringBefore("|").trim().ifBlank { "Dirección no disponible" }

    /** Extrae lat/lng de la parte después de "|" ("lat,lng"). */
    private fun parseLatLng(location: String): Pair<Double, Double>? {
        val coords = location.substringAfter("|", "").trim()
        if (coords.isBlank()) return null
        val parts = coords.split(",")
        if (parts.size < 2) return null
        val lat = parts[0].trim().toDoubleOrNull() ?: return null
        val lng = parts[1].trim().toDoubleOrNull() ?: return null
        return lat to lng
    }

    private fun distanceTextTo(user: Location?, salon: Pair<Double, Double>?): String? {
        if (user == null || salon == null) return null
        val out = FloatArray(1)
        Location.distanceBetween(user.latitude, user.longitude, salon.first, salon.second, out)
        val meters = out[0]
        return if (meters < 1000) "${meters.roundToInt()} m"
        else String.format("%.1f km", meters / 1000)
    }
}

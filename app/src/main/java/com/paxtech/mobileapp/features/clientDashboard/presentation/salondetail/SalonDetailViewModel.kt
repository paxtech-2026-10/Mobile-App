package com.paxtech.mobileapp.features.clientDashboard.presentation.salondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.core.geocoding.GeocodingRepository
import com.paxtech.mobileapp.core.utils.LocationUtils
import com.paxtech.mobileapp.features.clientDashboard.domain.model.RatingSummary
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.LocalSalonRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.ReviewRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.SalonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ReviewUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.AboutUi
import com.paxtech.mobileapp.features.services.domain.ServiceRepository
import com.paxtech.mobileapp.shared.model.Salon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SalonDetailViewModel @Inject constructor(
    private val salonRepository: SalonRepository,
    private val serviceRepository: ServiceRepository,
    private val reviewRepository: ReviewRepository,
    private val geocodingRepository: GeocodingRepository,
    private val localRepository: LocalSalonRepository
) : ViewModel() {
    private val _salon = MutableStateFlow<Salon?>(null)
    val salon: StateFlow<Salon?> = _salon

    private val _services = MutableStateFlow<List<ServiceUi>>(emptyList())
    val services: StateFlow<List<ServiceUi>> = _services

    private val _reviews = MutableStateFlow<List<ReviewUi>>(emptyList())
    val reviews: StateFlow<List<ReviewUi>> = _reviews

    private val _about = MutableStateFlow<AboutUi>(
        AboutUi(
            email = "Cargando información...",
            socials = emptyMap(),
            ubicacion = "Cargando dirección..."
        )
    )
    val about: StateFlow<AboutUi> = _about
    
    // Rating del salón
    private val _ratingSummary = MutableStateFlow<RatingSummary?>(null)
    val ratingSummary: StateFlow<RatingSummary?> = _ratingSummary.asStateFlow()
    
    // Dirección real del salón (obtenida por geocoding)
    private val _salonAddress = MutableStateFlow<String?>(null)
    val salonAddress: StateFlow<String?> = _salonAddress.asStateFlow()
    
    // Estado de favorito
    private val _isFavorite = MutableStateFlow<Boolean>(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun load(salonId: Int) {
        viewModelScope.launch {
            try {
                // Cargar datos del salón desde el API
                val salonData = salonRepository.getSalonById(salonId)

                if (salonData != null) {
                    _salon.value = salonData

                    // Cargar rating del salón
                    loadRatingSummary(salonId)
                    
                    // Cargar dirección real usando geocoding
                    loadSalonAddress(salonData.location)
                    
                    // Verificar si es favorito
                    checkFavoriteStatus(salonId)

                    // Actualizar la información "About" con datos reales del salón
                    // La ubicación se actualizará cuando se cargue la dirección real
                    _about.value = AboutUi(
                        email = salonData.email,
                        socials = salonData.socials,
                        ubicacion = salonData.location, // Se actualizará cuando se cargue la dirección
                    )
                } else {
                    // Si no se encuentra el salón en el API, usar datos mockeados
                    _salon.value = Salon(
                        id = salonId,
                        companyName = "Salón $salonId",
                        coverImageUrl = "https://images.unsplash.com/photo-1562322140-8baeececf3df?q=80&w=1000",
                        location = "",
                        email = " ",
                        socials = emptyMap()
                    )
                }

                /*
                _services.value = listOf(
                    ServiceUi("1", "Corte simple", "Corte de cabello básico", "s/40.00", 50),
                    ServiceUi("2", "Corte + Lavado", "Corte completo con lavado", "s/60.00", 75),
                    ServiceUi("3", "Coloración", "Tinte completo del cabello", "s/120.00", 120)
                )*/
                try {
                    val salonService = serviceRepository.getServiceByProviderId(salonId)
                    _services.value = salonService.map { service->
                        ServiceUi(
                            id = service.id.toString(),
                            title = service.name,
                            subtitle = "", // Valor por defecto vacío, se puede usar descripción si está disponible en el futuro
                            price = "s/${service.price}.00",
                            durationMins = service.duration
                        )
                    }
                }catch (e: Exception) {
                    println("🔍 SalonDetailViewModel: Error loading services for salon $salonId: ${e.message}")
                    // Mantener servicios mockeados en caso de error
                    _services.value = listOf(
                        ServiceUi("1", "Servicio no disponible", "Error al cargar servicios", "s/0.00", 0)
                    )
                }

                try {
                    val salonReviews = reviewRepository.getReviewsByProviderId(salonId)
                    _reviews.value = salonReviews
                } catch (e: Exception) {
                    println("🔍 SalonDetailViewModel: Error loading reviews for salon $salonId: ${e.message}")
                    // Mantener reseñas mockeadas en caso de error
                    _reviews.value = listOf(
                        ReviewUi("Cliente", 5, "No hay reseñas disponibles")
                    )
                }

            } catch (e: Exception) {
                println("🔍 SalonDetailViewModel: Error loading salon $salonId: ${e.message}")
                // En caso de error, usar datos mockeados
                _salon.value = Salon(
                    id = salonId,
                    companyName = "Salón $salonId",
                    coverImageUrl = "https://images.unsplash.com/photo-1562322140-8baeececf3df?q=80&w=1000",
                    location = "",
                    email = " ",
                    socials = emptyMap()
                )
            }
        }
    }
    
    /**
     * Carga el rating summary del salón
     */
    private suspend fun loadRatingSummary(salonId: Int) {
        try {
            val rating = reviewRepository.getRatingSummary(salonId)
            _ratingSummary.value = rating
            if (rating != null) {
                println("🔍 SalonDetailViewModel: Rating loaded: ${rating.averageRating} (${rating.reviewCount} reviews)")
            } else {
                println("🔍 SalonDetailViewModel: No rating found for salon $salonId")
            }
        } catch (e: Exception) {
            println("🔍 SalonDetailViewModel: Error loading rating: ${e.message}")
        }
    }
    
    /**
     * Carga la dirección real del salón usando geocoding
     */
    private suspend fun loadSalonAddress(locationString: String) {
        try {
            val coordinates = LocationUtils.parseCoordinates(locationString)
            if (coordinates != null) {
                val address = geocodingRepository.reverseGeocode(coordinates.first, coordinates.second)
                if (address != null) {
                    _salonAddress.value = address
                    // Actualizar About con la dirección real
                    _about.value = _about.value.copy(ubicacion = address)
                    println("🔍 SalonDetailViewModel: Address loaded: $address")
                } else {
                    // Si no se puede obtener la dirección, usar la extraída del string
                    val extractedAddress = LocationUtils.extractAddress(locationString)
                    _salonAddress.value = extractedAddress
                    _about.value = _about.value.copy(ubicacion = extractedAddress)
                    println("🔍 SalonDetailViewModel: Using extracted address: $extractedAddress")
                }
            } else {
                // Si no hay coordenadas, usar el string original
                _salonAddress.value = locationString
                _about.value = _about.value.copy(ubicacion = locationString)
                println("🔍 SalonDetailViewModel: No coordinates found, using original location string")
            }
        } catch (e: Exception) {
            println("🔍 SalonDetailViewModel: Error loading address: ${e.message}")
            // En caso de error, usar el string original
            _salonAddress.value = locationString
            _about.value = _about.value.copy(ubicacion = locationString)
        }
    }
    
    /**
     * Verifica si el salón está en favoritos
     */
    private suspend fun checkFavoriteStatus(salonId: Int) {
        try {
            _isFavorite.value = localRepository.isFavorite(salonId)
        } catch (e: Exception) {
            println("🔍 SalonDetailViewModel: Error checking favorite status: ${e.message}")
            _isFavorite.value = false
        }
    }
    
    /**
     * Alterna el estado de favorito del salón
     */
    fun toggleFavorite() {
        val currentSalon = _salon.value
        if (currentSalon != null) {
            viewModelScope.launch {
                try {
                    val newStatus = localRepository.toggleFavorite(currentSalon)
                    _isFavorite.value = newStatus
                    println("🔍 SalonDetailViewModel: Favorite toggled to: $newStatus")
                } catch (e: Exception) {
                    println("🔍 SalonDetailViewModel: Error toggling favorite: ${e.message}")
                }
            }
        }
    }
}
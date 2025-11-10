package com.paxtech.mobileapp.features.clientDashboard.presentation.home

import com.paxtech.mobileapp.features.clientDashboard.domain.repository.LocalSalonRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.SalonRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.ReviewRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.model.RatingSummary
import com.paxtech.mobileapp.features.authentication.domain.repository.UserDataRepository
import com.paxtech.mobileapp.core.location.LocationManager
import com.paxtech.mobileapp.core.utils.LocationUtils
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.shared.model.Salon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SalonRepository,
    private val localRepository: LocalSalonRepository,
    private val userDataRepository: UserDataRepository,
    private val reviewRepository: ReviewRepository,
    private val locationManager: LocationManager
): ViewModel() {
    
    private val _recommendedSalons = MutableStateFlow<List<Salon>>(emptyList())
    val recommendedSalons: StateFlow<List<Salon>> = _recommendedSalons.asStateFlow()
    
    private val _favoriteSalons = MutableStateFlow<List<Salon>>(emptyList())
    val favoriteSalons: StateFlow<List<Salon>> = _favoriteSalons.asStateFlow()
    
    private val _recentVisits = MutableStateFlow<List<Salon>>(emptyList())
    val recentVisits: StateFlow<List<Salon>> = _recentVisits.asStateFlow()
    
    private val _userName = MutableStateFlow<String>("Usuario")
    val userName: StateFlow<String> = _userName.asStateFlow()

    //Buscador de salones
    private val _searchResults = MutableStateFlow<List<Salon>>(emptyList())
    val searchResults: StateFlow<List<Salon>> = _searchResults.asStateFlow()
    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _isSearching = MutableStateFlow<Boolean>(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    private var searchJob: Job? = null

    // Ratings de salones
    private val _salonRatings = MutableStateFlow<Map<Int, RatingSummary>>(emptyMap())
    val salonRatings: StateFlow<Map<Int, RatingSummary>> = _salonRatings.asStateFlow()
    
    // Ubicación del usuario
    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation.asStateFlow()
    
    // Estado de permisos de ubicación
    private val _hasLocationPermission = MutableStateFlow<Boolean>(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()
    
    // Salones con distancias calculadas y ordenados
    private val _salonsWithDistance = MutableStateFlow<List<Pair<Salon, Float>>>(emptyList())
    val salonsWithDistance: StateFlow<List<Pair<Salon, Float>>> = _salonsWithDistance.asStateFlow()

    fun loadAllData(){
        viewModelScope.launch {
            println("🔍 HomeViewModel: Loading all data...")
            
            // Verificar permisos de ubicación
            checkLocationPermission()
            
            // Cargar salones recomendados del API
            val salons = repository.getAllSalons()
            println("🔍 HomeViewModel: Received ${salons.size} recommended salons")
            _recommendedSalons.value = salons
            
            // Cargar ratings para todos los salones
            loadSalonRatings(salons)
            
            // Obtener ubicación del usuario
            loadUserLocation()
            
            // Calcular distancias y ordenar salones
            calculateSalonDistances(salons)
            
            // Cargar favoritos locales
            val favorites = localRepository.getAllFavorites()
            println("🔍 HomeViewModel: Received ${favorites.size} favorite salons")
            _favoriteSalons.value = favorites
            
            // Cargar visitas recientes locales
            val recent = localRepository.getRecentVisits()
            println("🔍 HomeViewModel: Received ${recent.size} recent visits")
            _recentVisits.value = recent
            
            // Cargar nombre del usuario
            loadUserName()
        }
    }
    
    fun checkLocationPermission() {
        _hasLocationPermission.value = locationManager.hasLocationPermission()
    }
    
    fun reloadLocationAndDistances() {
        viewModelScope.launch {
            checkLocationPermission()
            if (locationManager.hasLocationPermission()) {
                loadUserLocation()
                // Recalcular distancias con los salones actuales
                calculateSalonDistances(_recommendedSalons.value)
            }
        }
    }
    
    private suspend fun loadUserLocation() {
        try {
            if (!locationManager.hasLocationPermission()) {
                println("🔍 HomeViewModel: No location permission, skipping location load")
                return
            }
            
            val location = locationManager.getCurrentLocation()
            _userLocation.value = location
            if (location != null) {
                println("🔍 HomeViewModel: User location loaded: ${location.latitude}, ${location.longitude}")
            } else {
                println("🔍 HomeViewModel: Could not get user location")
            }
        } catch (e: Exception) {
            println("🔍 HomeViewModel: Error loading user location: ${e.message}")
        }
    }
    
    private suspend fun calculateSalonDistances(salons: List<Salon>) {
        val userLocation = _userLocation.value
        
        if (userLocation != null) {
            println("🔍 HomeViewModel: Calculating distances from user location: ${userLocation.latitude}, ${userLocation.longitude}")
            val salonsWithDist = salons.mapNotNull { salon ->
                println("🔍 HomeViewModel: Processing salon '${salon.companyName}' with location string: '${salon.location}'")
                val coordinates = LocationUtils.parseCoordinates(salon.location)
                if (coordinates != null) {
                    println("🔍 HomeViewModel: Parsed coordinates for '${salon.companyName}': lat=${coordinates.first}, lng=${coordinates.second}")
                    val distance = LocationUtils.calculateDistance(
                        userLocation.latitude,
                        userLocation.longitude,
                        coordinates.first,
                        coordinates.second
                    )
                    println("🔍 HomeViewModel: Distance for '${salon.companyName}': ${distance}km")
                    salon to distance
                } else {
                    println("🔍 HomeViewModel: Could not parse coordinates for '${salon.companyName}' from: '${salon.location}'")
                    // Si no se pueden parsear coordenadas, incluir con distancia máxima para que aparezca al final
                    salon to Float.MAX_VALUE
                }
            }.sortedBy { it.second } // Ordenar por distancia (más cercanos primero)
            
            _salonsWithDistance.value = salonsWithDist
            println("🔍 HomeViewModel: Calculated distances for ${salonsWithDist.size} salons")
        } else {
            // Si no hay ubicación, mostrar salones sin distancia
            _salonsWithDistance.value = salons.map { it to Float.MAX_VALUE }
            println("🔍 HomeViewModel: No user location, salons shown without distance")
        }
    }
    
    private suspend fun loadSalonRatings(salons: List<Salon>) {
        val ratingsMap = mutableMapOf<Int, RatingSummary>()
        
        salons.forEach { salon ->
            try {
                val ratingSummary = reviewRepository.getRatingSummary(salon.id)
                if (ratingSummary != null) {
                    ratingsMap[salon.id] = ratingSummary
                    println("🔍 HomeViewModel: Loaded rating for salon ${salon.id}: ${ratingSummary.averageRating} (${ratingSummary.reviewCount} reviews)")
                }
            } catch (e: Exception) {
                println("🔍 HomeViewModel: Error loading rating for salon ${salon.id}: ${e.message}")
            }
        }
        
        _salonRatings.value = ratingsMap
        println("🔍 HomeViewModel: Loaded ratings for ${ratingsMap.size} salons")
    }
    
    fun loadUserName() {
        _userName.value = userDataRepository.getUserName()
    }
    
    fun saveVisit(salon: Salon) {
        viewModelScope.launch {
            localRepository.saveSalonToHistory(salon)
            // Refrescar la lista de visitas recientes
            _recentVisits.value = localRepository.getRecentVisits()
        }
    }
    
    fun toggleFavorite(salon: Salon) {
        viewModelScope.launch {
            localRepository.toggleFavorite(salon)
            // Refrescar la lista de favoritos
            _favoriteSalons.value = localRepository.getAllFavorites()
        }
    }

    fun updateSearchQuery(query: String){
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()){
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        if (query.length < 2){
            _searchResults.value = emptyList()
            return
        }
        _isSearching.value = true
        searchJob = viewModelScope.launch {
            delay(300)

            try {
                val results = repository.getSalonByName(query)
                _searchResults.value = results
                println("🔍 HomeViewModel: Search results: $results")
            } catch (e: Exception){
                println("🔍 HomeViewModel: Error searching: ${e.message}")
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearSearch(){
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _isSearching.value = false
        searchJob?.cancel()
    }

    init {
        loadAllData()
        loadUserName()
    }
}
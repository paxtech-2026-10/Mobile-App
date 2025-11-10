package com.paxtech.mobileapp.features.clientDashboard.presentation.home

import com.paxtech.mobileapp.features.clientDashboard.domain.repository.LocalSalonRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.SalonRepository
import com.paxtech.mobileapp.features.authentication.domain.repository.UserDataRepository
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
import kotlinx.coroutines.time.delay
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SalonRepository,
    private val localRepository: LocalSalonRepository,
    private val userDataRepository: UserDataRepository
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


    fun loadAllData(){
        viewModelScope.launch {
            println("🔍 HomeViewModel: Loading all data...")
            
            // Cargar salones recomendados del API
            val salons = repository.getAllSalons()
            println("🔍 HomeViewModel: Received ${salons.size} recommended salons")
            _recommendedSalons.value = salons
            
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
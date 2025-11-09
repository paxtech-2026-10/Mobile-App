package com.paxtech.mobileapp.features.clientDashboard.presentation.home

import com.paxtech.mobileapp.features.clientDashboard.domain.repository.LocalSalonRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.SalonRepository
import com.paxtech.mobileapp.features.authentication.domain.repository.UserDataRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.shared.model.Salon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    init {
        loadAllData()
        loadUserName()
    }
}
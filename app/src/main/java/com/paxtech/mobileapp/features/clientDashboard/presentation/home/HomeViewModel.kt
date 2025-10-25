package com.paxtech.mobileapp.features.clientDashboard.presentation.home


import com.paxtech.mobileapp.features.clientDashboard.domain.domain.SalonRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.shared.model.Salon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: SalonRepository): ViewModel() {
    private val _salons = MutableStateFlow<List<Salon>>(emptyList())
    val salons: StateFlow<List<Salon>> = _salons

    fun getAllSalons(){
        viewModelScope.launch {
            println("🔍 HomeViewModel: Calling getAllSalons...")
            val salons = repository.getAllSalons()
            println("🔍 HomeViewModel: Received ${salons.size} salons")
            _salons.value = salons
        }
    }

    init {
        getAllSalons()
    }
}
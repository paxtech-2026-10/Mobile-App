package com.paxtech.mobileapp.features.clientDashboard.presentation.reservations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.core.geocoding.GeocodingRepository
import com.paxtech.mobileapp.core.utils.LocationUtils
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReservationRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.SalonRepository
import com.paxtech.mobileapp.shared.model.Salon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationDetailViewModel @Inject constructor(
    private val salonRepository: SalonRepository,
    private val geocodingRepository: GeocodingRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {
    
    private val _isCancelling = MutableStateFlow(false)
    val isCancelling: StateFlow<Boolean> = _isCancelling.asStateFlow()
    
    private val _cancelResult = MutableStateFlow<Result<Unit>?>(null)
    val cancelResult: StateFlow<Result<Unit>?> = _cancelResult.asStateFlow()
    
    private val _salon = MutableStateFlow<Salon?>(null)
    val salon: StateFlow<Salon?> = _salon.asStateFlow()
    
    private val _salonAddress = MutableStateFlow<String?>(null)
    val salonAddress: StateFlow<String?> = _salonAddress.asStateFlow()
    
    fun loadSalonInfo(providerId: Int) {
        viewModelScope.launch {
            try {
                val salonData = salonRepository.getSalonById(providerId)
                _salon.value = salonData
                
                // Cargar dirección real usando geocoding
                if (salonData != null) {
                    loadSalonAddress(salonData.location)
                }
            } catch (e: Exception) {
                println("🔍 ReservationDetailViewModel: Error loading salon info: ${e.message}")
            }
        }
    }
    
    private suspend fun loadSalonAddress(locationString: String) {
        try {
            val coordinates = LocationUtils.parseCoordinates(locationString)
            if (coordinates != null) {
                val address = geocodingRepository.reverseGeocode(coordinates.first, coordinates.second)
                if (address != null) {
                    _salonAddress.value = address
                } else {
                    val extractedAddress = LocationUtils.extractAddress(locationString)
                    _salonAddress.value = extractedAddress
                }
            } else {
                _salonAddress.value = locationString
            }
        } catch (e: Exception) {
            println("🔍 ReservationDetailViewModel: Error loading address: ${e.message}")
            _salonAddress.value = locationString
        }
    }
    
    fun cancelReservation(reservationId: Long) {
        viewModelScope.launch {
            _isCancelling.value = true
            _cancelResult.value = null
            
            reservationRepository.cancelReservation(reservationId)
                .onSuccess {
                    _cancelResult.value = Result.success(Unit)
                    println("🔍 ReservationDetailViewModel: Reservation $reservationId cancelled successfully")
                }
                .onFailure { exception ->
                    _cancelResult.value = Result.failure(exception)
                    println("🔍 ReservationDetailViewModel: Error cancelling reservation: ${exception.message}")
                }
            
            _isCancelling.value = false
        }
    }
    
    fun resetCancelResult() {
        _cancelResult.value = null
    }
    
    fun resetState() {
        _cancelResult.value = null
        _isCancelling.value = false
        _salon.value = null
        _salonAddress.value = null
    }
}


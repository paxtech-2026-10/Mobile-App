package com.paxtech.mobileapp.features.clientDashboard.presentation.reservations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.authentication.domain.repository.UserDataRepository
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReservationRepository
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReservationDetailsDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class ReservationFilter {
    ALL, UPCOMING, COMPLETED, PENDING
}

@HiltViewModel
class ReservationsViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    
    private val _reservations = MutableStateFlow<List<ReservationDetailsDto>>(emptyList())
    val reservations: StateFlow<List<ReservationDetailsDto>> = _reservations.asStateFlow()
    
    private val _selectedFilter = MutableStateFlow(ReservationFilter.ALL)
    val selectedFilter: StateFlow<ReservationFilter> = _selectedFilter.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadReservations()
    }
    
    fun loadReservations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            //Conseguir id del cliente
            val clientId = userDataRepository.getUserId().toLong();
            
            reservationRepository.getAllDetails(clientId)
                .onSuccess { reservations ->
                    _reservations.value = reservations
                    println("🔍 ReservationsViewModel: Loaded ${reservations.size} reservations")
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error loading reservations"
                    println("🔍 ReservationsViewModel: Error loading reservations: ${exception.message}")
                }
            
            _isLoading.value = false
        }
    }
    
    fun setFilter(filter: ReservationFilter) {
        _selectedFilter.value = filter
    }
    
    fun getFilteredReservations(): List<ReservationDetailsDto> {
        val allReservations = _reservations.value
        val filter = _selectedFilter.value
        
        return when (filter) {
            ReservationFilter.ALL -> allReservations
            ReservationFilter.UPCOMING -> allReservations.filter { isUpcoming(it) }
            ReservationFilter.COMPLETED -> allReservations.filter { isCompleted(it) }
            ReservationFilter.PENDING -> allReservations.filter { isPending(it) }
        }
    }
    
    private fun isUpcoming(reservation: ReservationDetailsDto): Boolean {
        val startTime = parseDate(reservation.timeSlot.startTime)
        return startTime != null && startTime.after(Date())
    }
    
    private fun isCompleted(reservation: ReservationDetailsDto): Boolean {
        val endTime = parseDate(reservation.timeSlot.endTime)
        return endTime != null && endTime.before(Date())
    }
    
    private fun isPending(reservation: ReservationDetailsDto): Boolean {
        // Si el timeSlot tiene status false, podría ser pendiente
        // O si la fecha está en el futuro pero muy próxima
        return !reservation.timeSlot.status
    }
    
    private fun parseDate(dateString: String): Date? {
        return try {
            // Intentar parsear el formato ISO 8601
            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX"
            )
            
            for (format in formats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    return sdf.parse(dateString)
                } catch (e: Exception) {
                    continue
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
    
    fun formatDate(dateString: String): String {
        val date = parseDate(dateString) ?: return dateString
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy - hh:mm a", Locale.getDefault())
        return sdf.format(date)
    }
}


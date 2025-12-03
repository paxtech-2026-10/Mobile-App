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
            println("🔍🔍 ReservationsViewModel: ===== INICIANDO CARGA DE RESERVACIONES =====")
            _isLoading.value = true
            _error.value = null

            //Conseguir id del cliente
            val clientId = userDataRepository.getClientId().toLong()
            println("🔍 ReservationsViewModel: ClientId obtenido de SharedPreferences: $clientId")
            
            println("🔍 ReservationsViewModel: Llamando a reservationRepository.getAllDetails($clientId)")
            reservationRepository.getAllDetailsByClientId(clientId)
                .onSuccess { reservations ->
                    println("🔍 ReservationsViewModel: ✅ ÉXITO - Se recibieron ${reservations.size} reservaciones")
                    if (reservations.isEmpty()) {
                        println("⚠️ ReservationsViewModel: ADVERTENCIA - La lista de reservaciones está vacía")
                    } else {
                        reservations.forEachIndexed { index, reservation ->
                            println("🔍 ReservationsViewModel: Reservación $index:")
                            println("   - ID: ${reservation.id}")
                            println("   - ClientId: ${reservation.clientId}")
                            println("   - Service: ${reservation.serviceId.name}")
                            println("   - Provider: ${reservation.provider.companyName}")
                            println("   - TimeSlot: ${reservation.timeSlot.startTime} - ${reservation.timeSlot.endTime}")
                        }
                    }
                    _reservations.value = reservations
                    println("🔍 ReservationsViewModel: Estado actualizado con ${reservations.size} reservaciones")
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error loading reservations"
                    println("❌ ReservationsViewModel: ERROR al cargar reservaciones")
                    println("   - Mensaje: ${exception.message}")
                    println("   - Tipo: ${exception.javaClass.simpleName}")
                    exception.printStackTrace()
                }
            
            _isLoading.value = false
            println("🔍🔍 ReservationsViewModel: ===== FIN DE CARGA DE RESERVACIONES =====")
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
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss"  // Formato sin 'Z' ni milisegundos (ej: "2025-11-11T16:15:00")
            )
            
            for (format in formats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.getDefault())
                    // Solo establecer timeZone si el formato incluye 'Z' o zona horaria
                    if (format.contains("Z") || format.contains("XXX")) {
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                    }
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



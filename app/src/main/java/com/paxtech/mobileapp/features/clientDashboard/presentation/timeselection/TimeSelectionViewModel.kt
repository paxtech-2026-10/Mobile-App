package com.paxtech.mobileapp.features.clientDashboard.presentation.timeselection

import android.service.autofill.UserData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.authentication.domain.repository.UserDataRepository
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReservationRepository
import com.paxtech.mobileapp.features.clientDashboard.data.repository.TimeSlot
import com.paxtech.mobileapp.features.clientDashboard.data.repository.TimeSlotRepository
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateReservationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@HiltViewModel
class TimeSelectionViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val timeSlotRepository: TimeSlotRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _bookedTimeSlots = MutableStateFlow<Set<String>>(emptySet())
    val bookedTimeSlots: StateFlow<Set<String>> = _bookedTimeSlots

    private val _timeSlotMap = MutableStateFlow<Map<String, Long>>(emptyMap())
    val timeSlotMap: StateFlow<Map<String, Long>> = _timeSlotMap

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Soporta múltiples formatos de fecha del backend
    private val inputFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US), // Formato del backend: 2025-11-02T16:30:00
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US), // Formato ISO con milisegundos
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US) // Formato ISO sin milisegundos
    )
    private val outputFormat = SimpleDateFormat("hh:mm a", Locale.US)
    
    private fun parseTime(timeString: String): java.util.Date? {
        for (format in inputFormats) {
            try {
                return format.parse(timeString)
            } catch (e: Exception) {
                continue
            }
        }
        return null
    }

    fun load(workerId: Long, providerId: Long? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val clientId = userDataRepository.getUserId().toLong();

            // Cargar time slots del backend para mapear horarios a IDs
            val slots = timeSlotRepository.getAll()
            slots.onSuccess { list ->
                val map = list.mapNotNull { slot ->
                    parseTime(slot.startTime)?.let { 
                        outputFormat.format(it) to slot.id
                    }
                }.toMap()
                _timeSlotMap.value = map
                println("🔍 TimeSelectionViewModel: Mapped ${map.size} time slots: $map")
            }.onFailure { _errorMessage.value = it.message }

            // Cargar reservas del backend para marcar horarios ocupados
            val reservations = reservationRepository.getAllDetails(clientId)
            reservations.onSuccess { details ->
                println("🔍 TimeSelectionViewModel: Loaded ${details.size} reservations")
                
                // Filtrar reservas del workerId (y providerId si está disponible)
                val filteredReservations = details.filter { reservation ->
                    val matchesWorker = reservation.workerId.id == workerId
                    val matchesProvider = providerId == null || reservation.provider.id == providerId
                    matchesWorker && matchesProvider
                }
                
                println("🔍 TimeSelectionViewModel: Filtered ${filteredReservations.size} reservations for workerId=$workerId, providerId=$providerId")
                
                // Convertir horarios a formato "hh:mm a"
                val bookedTimes = filteredReservations.mapNotNull { reservation ->
                    val timeString = reservation.timeSlot.startTime
                    val parsed = parseTime(timeString)
                    val formatted = parsed?.let { outputFormat.format(it) }
                    if (formatted != null) {
                        println("🔍 TimeSelectionViewModel: Booked time: $timeString -> $formatted")
                    }
                    formatted
                }.toSet()
                
                println("🔍 TimeSelectionViewModel: Final booked times: $bookedTimes")
                _bookedTimeSlots.value = bookedTimes
            }.onFailure { 
                println("🔍 TimeSelectionViewModel: Error loading reservations: ${it.message}")
                _errorMessage.value = it.message
            }

            _isLoading.value = false
        }
    }

    fun getTimeSlotId(timeString: String): Long? {
        return _timeSlotMap.value[timeString]
    }

    fun createReservation(clientId: Long, providerId: Long, workerId: Long, timeSlotId: Long, paymentId: Long = 0L, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = reservationRepository.createReservation(
                CreateReservationRequest(clientId, providerId, paymentId, timeSlotId, workerId)
            )
            if (result.isSuccess) onDone(true, null) else onDone(false, result.exceptionOrNull()?.message)
        }
    }
}


package com.paxtech.mobileapp.features.clientDashboard.presentation.timeselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReservationRepository
import com.paxtech.mobileapp.features.clientDashboard.data.repository.TimeSlot
import com.paxtech.mobileapp.features.clientDashboard.data.repository.TimeSlotRepository
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateReservationRequest
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateTimeSlotRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@HiltViewModel
class TimeSelectionViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val timeSlotRepository: TimeSlotRepository
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
            val reservations = reservationRepository.getAllDetails()
            reservations.onSuccess { details ->
                println("🔍 TimeSelectionViewModel: Loaded ${details.size} reservations from backend")
                
                // Filtrar reservas del workerId (y providerId si está disponible)
                val filteredReservations = details.filter { reservation ->
                    val matchesWorker = reservation.workerId.id == workerId
                    val matchesProvider = providerId == null || reservation.provider.id == providerId
                    val matches = matchesWorker && matchesProvider
                    if (matches) {
                        println("🔍 TimeSelectionViewModel: Reserva coincide - workerId=${reservation.workerId.id}, providerId=${reservation.provider.id}")
                    }
                    matches
                }
                
                println("🔍 TimeSelectionViewModel: Filtered ${filteredReservations.size} reservations for workerId=$workerId, providerId=$providerId")
                
                // Convertir horarios a formato "hh:mm a"
                val bookedTimes = filteredReservations.mapNotNull { reservation ->
                    val timeString = reservation.timeSlot.startTime
                    val parsed = parseTime(timeString)
                    val formatted = parsed?.let { outputFormat.format(it) }
                    if (formatted != null) {
                        println("🔍 TimeSelectionViewModel: Booked time: $timeString -> $formatted")
                    } else {
                        println("🔍 TimeSelectionViewModel: No se pudo parsear el tiempo: $timeString")
                    }
                    formatted
                }.toSet()
                
                println("🔍 TimeSelectionViewModel: Final booked times (${bookedTimes.size}): $bookedTimes")
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

    fun createReservation(clientId: Long, providerId: Long, workerId: Long, timeSlotId: Long, serviceId: Long, selectedTime: String, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            println("🔍 TimeSelectionViewModel: Creando reserva - clientId=$clientId, providerId=$providerId, workerId=$workerId, timeSlotId=$timeSlotId, serviceId=$serviceId")
            val result = reservationRepository.createReservation(
                CreateReservationRequest(clientId, providerId, serviceId, timeSlotId, workerId)
            )
            result.onSuccess {
                // Marcar el horario como reservado localmente
                markTimeSlotAsBooked(selectedTime)
                println("🔍 TimeSelectionViewModel: Reserva creada exitosamente, horario marcado como reservado: $selectedTime")
                onDone(true, null)
            }.onFailure { error ->
                println("🔍 TimeSelectionViewModel: Error al crear reserva: ${error.message}")
                onDone(false, error.message)
            }
        }
    }

    private fun markTimeSlotAsBooked(timeString: String) {
        val currentBooked = _bookedTimeSlots.value.toMutableSet()
        currentBooked.add(timeString)
        _bookedTimeSlots.value = currentBooked
        println("🔍 TimeSelectionViewModel: Marcado horario como reservado: $timeString")
        println("🔍 TimeSelectionViewModel: Horarios reservados actuales: ${_bookedTimeSlots.value}")
    }

    fun refreshBookedTimeSlots(workerId: Long, providerId: Long? = null) {
        viewModelScope.launch {
            val reservations = reservationRepository.getAllDetails()
            reservations.onSuccess { details ->
                // Filtrar reservas del workerId (y providerId si está disponible)
                val filteredReservations = details.filter { reservation ->
                    val matchesWorker = reservation.workerId.id == workerId
                    val matchesProvider = providerId == null || reservation.provider.id == providerId
                    matchesWorker && matchesProvider
                }
                
                // Convertir horarios a formato "hh:mm a"
                val bookedTimes = filteredReservations.mapNotNull { reservation ->
                    val timeString = reservation.timeSlot.startTime
                    val parsed = parseTime(timeString)
                    parsed?.let { outputFormat.format(it) }
                }.toSet()
                
                _bookedTimeSlots.value = bookedTimes
                println("🔍 TimeSelectionViewModel: Horarios ocupados actualizados: $bookedTimes")
            }.onFailure { 
                println("🔍 TimeSelectionViewModel: Error al actualizar horarios ocupados: ${it.message}")
            }
        }
    }

    fun createTimeSlot(
        selectedDate: Calendar,
        selectedTime: String,
        serviceDuration: Int,
        onDone: (Result<TimeSlot>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Parsear el horario seleccionado (formato "hh:mm a")
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                val parsedTime = timeFormat.parse(selectedTime)
                    ?: throw IllegalArgumentException("No se pudo parsear el horario: $selectedTime")

                // Crear Calendar con la fecha y hora seleccionada
                val startCalendar = Calendar.getInstance().apply {
                    // Copiar la fecha seleccionada
                    set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
                    set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
                    
                    // Parsear y establecer la hora
                    val timeCalendar = Calendar.getInstance().apply {
                        time = parsedTime
                    }
                    set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // Calcular endTime sumando la duración
                val endCalendar = Calendar.getInstance().apply {
                    timeInMillis = startCalendar.timeInMillis
                    add(Calendar.MINUTE, serviceDuration)
                }

                // Formatear a ISO 8601 con Z (UTC)
                // El formato con 'Z' indica UTC, así que convertimos la hora local a UTC
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                
                // Formatear directamente - SimpleDateFormat con timeZone UTC convertirá automáticamente
                val startTimeISO = isoFormat.format(startCalendar.time)
                val endTimeISO = isoFormat.format(endCalendar.time)

                println("🔍 TimeSelectionViewModel: Creating time slot - startTime: $startTimeISO, endTime: $endTimeISO")

                // Crear el request según el Swagger
                val request = CreateTimeSlotRequest(
                    startTime = startTimeISO,
                    endTime = endTimeISO,
                    status = true,
                    type = "service" // Tipo por defecto, puede ajustarse según necesidad
                )

                val result = timeSlotRepository.createTimeSlot(request)
                onDone(result)
            } catch (e: Exception) {
                println("🔍 TimeSelectionViewModel: Error creating time slot: ${e.message}")
                onDone(Result.failure(e))
            }
        }
    }
}


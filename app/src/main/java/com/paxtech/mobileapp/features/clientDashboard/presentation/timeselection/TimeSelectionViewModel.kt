package com.paxtech.mobileapp.features.clientDashboard.presentation.timeselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.authentication.domain.repository.UserDataRepository
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReservationRepository
import com.paxtech.mobileapp.features.clientDashboard.data.repository.TimeSlotRepository
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateReservationRequest
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
    private val timeSlotRepository: TimeSlotRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Cambiar a Map<fecha, Set<hora>> para poder filtrar por fecha
    private val _bookedTimeSlots = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val bookedTimeSlots: StateFlow<Map<String, Set<String>>> = _bookedTimeSlots

    // Cambiar a Map<"fecha|hora", id> para evitar conflictos
    private val _timeSlotMap = MutableStateFlow<Map<String, Long>>(emptyMap())
    val timeSlotMap: StateFlow<Map<String, Long>> = _timeSlotMap

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Soporta múltiples formatos de fecha del backend
    // IMPORTANTE: El backend usa LocalDateTime (sin zona horaria), así que las fechas sin 'Z' son hora local
    private val inputFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US), // Formato del backend: hora local (sin zona horaria)
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { 
            timeZone = TimeZone.getTimeZone("UTC") 
        }, // Formato ISO con milisegundos en UTC
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply { 
            timeZone = TimeZone.getTimeZone("UTC") 
        } // Formato ISO sin milisegundos en UTC
    )
    private val outputFormat = SimpleDateFormat("hh:mm a", Locale.US)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US) // Para extraer solo la fecha
    
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

            val clientId = userDataRepository.getClientId().toLong();
            println("🔍 TimeSelectionViewModel: ClientId obtenido: $clientId")

            // Cargar time slots del backend para mapear horarios a IDs
            val slots = timeSlotRepository.getAll()
            slots.onSuccess { list ->
                val map = list.mapNotNull { slot ->
                    parseTime(slot.startTime)?.let { date ->
                        val dateKey = dateFormat.format(date)
                        val timeKey = outputFormat.format(date)
                        val fullKey = "$dateKey|$timeKey" // Formato: "2025-11-10|10:00 AM"
                        fullKey to slot.id
                    }
                }.toMap()
                _timeSlotMap.value = map
                println("🔍 TimeSelectionViewModel: Mapped ${map.size} time slots")
            }.onFailure { _errorMessage.value = it.message }

            // Cargar reservas del backend para marcar horarios ocupados
            val reservations = reservationRepository.getAllDetails()
            reservations.onSuccess { details ->
                println("🔍 TimeSelectionViewModel: Loaded ${details.size} reservations")
                
                // Filtrar reservas del workerId (y providerId si está disponible)
                val filteredReservations = details.filter { reservation ->
                    val matchesWorker = reservation.workerId.id == workerId
                    val matchesProvider = providerId == null || reservation.provider.id == providerId
                    matchesWorker && matchesProvider
                }
                
                println("🔍 TimeSelectionViewModel: Filtered ${filteredReservations.size} reservations for workerId=$workerId, providerId=$providerId")
                
                // Agrupar reservas por fecha y hora
                val bookedByDate = mutableMapOf<String, MutableSet<String>>()
                filteredReservations.forEach { reservation ->
                    val timeString = reservation.timeSlot.startTime
                    val parsed = parseTime(timeString)
                    if (parsed != null) {
                        val dateKey = dateFormat.format(parsed)
                        val timeKey = outputFormat.format(parsed)
                        bookedByDate.getOrPut(dateKey) { mutableSetOf() }.add(timeKey)
                        println("🔍 TimeSelectionViewModel: Booked - Date: $dateKey, Time: $timeKey (from: $timeString)")
                    }
                }
                
                println("🔍 TimeSelectionViewModel: Final booked times by date: $bookedByDate")
                _bookedTimeSlots.value = bookedByDate
            }.onFailure { 
                println("🔍 TimeSelectionViewModel: Error loading reservations: ${it.message}")
                _errorMessage.value = it.message
            }

            _isLoading.value = false
        }
    }

    fun getTimeSlotId(selectedDate: java.util.Calendar, timeString: String): Long? {
        val dateKey = dateFormat.format(selectedDate.time)
        val fullKey = "$dateKey|$timeString"
        return _timeSlotMap.value[fullKey]
    }
    
    /**
     * Obtiene los time slots ocupados para una fecha específica
     */
    fun getBookedTimesForDate(selectedDate: java.util.Calendar): Set<String> {
        val dateKey = dateFormat.format(selectedDate.time)
        return _bookedTimeSlots.value[dateKey] ?: emptySet()
    }
    
    /**
     * Crea un time slot si no existe, o retorna el ID del existente
     * @param selectedDate Fecha seleccionada (Calendar)
     * @param selectedTime Hora seleccionada en formato "hh:mm a" (ej: "02:00 PM")
     * @param serviceDuration Duración del servicio en minutos
     * @param onResult Callback con el ID del time slot o error
     */
    fun createTimeSlotIfNeeded(
        selectedDate: java.util.Calendar,
        selectedTime: String,
        serviceDuration: Int,
        onResult: (Long?, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Parsear la hora seleccionada (ej: "02:00 PM")
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                val timeDate = timeFormat.parse(selectedTime)
                    ?: run {
                        onResult(null, "Formato de hora inválido: $selectedTime")
                        return@launch
                    }
                
                // Extraer horas y minutos del Date parseado
                val timeCalendar = java.util.Calendar.getInstance().apply {
                    time = timeDate
                }
                
                // Combinar fecha y hora
                val calendar = java.util.Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
                    set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                
                // Calcular endTime sumando la duración
                val endCalendar = java.util.Calendar.getInstance().apply {
                    timeInMillis = calendar.timeInMillis
                    add(Calendar.MINUTE, serviceDuration)
                }
                
                // Formatear a ISO 8601 SIN convertir a UTC (usar hora local)
                // El backend usa LocalDateTime que NO tiene zona horaria, así que enviamos hora local
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                // NO establecer timeZone para usar la zona horaria local del dispositivo
                val startTimeISO = isoFormat.format(calendar.time)
                val endTimeISO = isoFormat.format(endCalendar.time)
                
                println("🔍 TimeSelectionViewModel: Hora local seleccionada: ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}")
                println("🔍 TimeSelectionViewModel: ISO string generado (hora local): $startTimeISO")
                
                println("🔍 TimeSelectionViewModel: Creando time slot - startTime: $startTimeISO, endTime: $endTimeISO, duration: $serviceDuration min")
                
                // Intentar crear el time slot
                val result = timeSlotRepository.createTimeSlot(
                    startTime = startTimeISO,
                    endTime = endTimeISO,
                    status = true,
                    type = "service"
                )
                
                result.onSuccess { timeSlot ->
                    println("🔍 TimeSelectionViewModel: Time slot creado exitosamente - ID: ${timeSlot.id}")
                    // Actualizar el mapa con el nuevo time slot
                    val dateKey = dateFormat.format(calendar.time)
                    val timeKey = outputFormat.format(calendar.time)
                    val fullKey = "$dateKey|$timeKey"
                    _timeSlotMap.value = _timeSlotMap.value + (fullKey to timeSlot.id)
                    onResult(timeSlot.id, null)
                }.onFailure { exception ->
                    println("🔍 TimeSelectionViewModel: Error al crear time slot: ${exception.message}")
                    onResult(null, exception.message)
                }
            } catch (e: Exception) {
                println("🔍 TimeSelectionViewModel: Excepción al crear time slot: ${e.message}")
                onResult(null, e.message)
            }
        }
    }

    fun createReservation(clientId: Long, providerId: Long, serviceId: Long, timeSlotId: Long, workerId: Long, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            println("🔍 TimeSelectionViewModel: Creando reservación - clientId: $clientId, providerId: $providerId, serviceId: $serviceId, timeSlotId: $timeSlotId, workerId: $workerId")
            val result = reservationRepository.createReservation(
                CreateReservationRequest(clientId, providerId, serviceId, timeSlotId, workerId)
            )
            if (result.isSuccess) {
                println("🔍 TimeSelectionViewModel: Reservación creada exitosamente")
                onDone(true, null)
            } else {
                val error = result.exceptionOrNull()?.message
                println("🔍 TimeSelectionViewModel: Error al crear reservación: $error")
                onDone(false, error)
            }
        }
    }
}


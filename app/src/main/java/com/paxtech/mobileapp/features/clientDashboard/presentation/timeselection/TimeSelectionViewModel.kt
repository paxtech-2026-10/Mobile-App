package com.paxtech.mobileapp.features.clientDashboard.presentation.timeselection

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReservationRepository
import com.paxtech.mobileapp.features.clientDashboard.data.repository.TimeSlot
import com.paxtech.mobileapp.features.clientDashboard.data.repository.TimeSlotRepository
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateReservationRequest
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateTimeSlotRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
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
    @Named("auth_prefs") private val authPrefs: SharedPreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _bookedTimeSlots = MutableStateFlow<Set<String>>(emptySet())
    val bookedTimeSlots: StateFlow<Set<String>> = _bookedTimeSlots

    private val _timeSlotMap = MutableStateFlow<Map<String, Long>>(emptyMap())
    val timeSlotMap: StateFlow<Map<String, Long>> = _timeSlotMap
    
    // Mapa inverso: timeSlotId -> timeString para poder marcar horarios reservados
    private val _timeSlotIdToTimeMap = MutableStateFlow<Map<Long, String>>(emptyMap())

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Soporta múltiples formatos de fecha del backend
    private val inputFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US), // Formato del backend: 2025-11-06T12:30:00
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US), // Formato ISO con milisegundos y Z
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US), // Formato ISO sin milisegundos con Z
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US) // Formato ISO con milisegundos sin Z
    )
    private val outputFormat = SimpleDateFormat("hh:mm a", Locale.US)
    
    private fun parseTime(timeString: String): java.util.Date? {
        println("🔍 TimeSelectionViewModel: Intentando parsear tiempo: $timeString")
        for (format in inputFormats) {
            try {
                val parsed = format.parse(timeString)
                if (parsed != null) {
                    val formatted = outputFormat.format(parsed)
                    println("🔍 TimeSelectionViewModel: Parseado exitosamente con formato ${format.toPattern()} -> $formatted")
                    return parsed
                }
            } catch (e: Exception) {
                // Continuar con el siguiente formato
                continue
            }
        }
        println("🔍 TimeSelectionViewModel: No se pudo parsear el tiempo: $timeString")
        return null
    }

    fun load(workerId: Long, providerId: Long? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // PRIMERO: Cargar time slots del backend para mapear horarios a IDs
            val slots = timeSlotRepository.getAll()
            slots.onSuccess { list ->
                val map = list.mapNotNull { slot ->
                    parseTime(slot.startTime)?.let { 
                        val formatted = outputFormat.format(it)
                        println("🔍 TimeSelectionViewModel: Mapeando time slot - id=${slot.id}, startTime=${slot.startTime} -> $formatted")
                        formatted to slot.id
                    }
                }.toMap()
                _timeSlotMap.value = map
                
                // Crear mapa inverso: timeSlotId -> timeString
                val inverseMap = map.entries.associate { it.value to it.key }
                _timeSlotIdToTimeMap.value = inverseMap
                println("🔍 TimeSelectionViewModel: ✅ Mapped ${map.size} time slots: $map")
                println("🔍 TimeSelectionViewModel: ✅ Mapa inverso creado con ${inverseMap.size} entradas")
                
                // Cargar timeSlotIds reservados guardados localmente y marcarlos
                loadPersistedBookedTimeSlots(inverseMap)
            }.onFailure { 
                println("🔍 TimeSelectionViewModel: ❌ Error cargando time slots: ${it.message}")
                _errorMessage.value = it.message 
            }

            val currentBookedTimes = _bookedTimeSlots.value.toMutableSet()
            val reservations = reservationRepository.getAllDetails()
            reservations.onSuccess { details ->
                println("🔍 TimeSelectionViewModel: Loaded ${details.size} reservations from backend")
                val filteredReservations = details.filter { reservation ->
                    val matchesWorker = reservation.workerId.id == workerId
                    val matchesProvider = providerId == null || reservation.provider.id == providerId
                    val matches = matchesWorker && matchesProvider
                    if (matches) {
                        println("🔍 TimeSelectionViewModel: Reserva coincide - workerId=${reservation.workerId.id}, providerId=${reservation.provider.id}, timeSlotId=${reservation.timeSlot.id}")
                    }
                    matches
                }
                
                println("🔍 TimeSelectionViewModel: Filtered ${filteredReservations.size} reservations for workerId=$workerId, providerId=$providerId")

                val bookedTimesFromBackend = filteredReservations.mapNotNull { reservation ->
                    val timeSlotId = reservation.timeSlot.id
                    val timeString = reservation.timeSlot.startTime
                    println("🔍 TimeSelectionViewModel: Procesando reserva - timeSlotId=$timeSlotId, startTime=$timeString")
                    

                    val parsed = parseTime(timeString)
                    val formatted = parsed?.let { outputFormat.format(it) }
                    
                    if (formatted != null) {
                        println("🔍 TimeSelectionViewModel: ✅ Booked time convertido: $timeString -> $formatted")
                        formatted
                    } else {
                        // Si no se puede parsear, usar el timeSlotIdToTimeMap como respaldo
                        val foundTime = _timeSlotIdToTimeMap.value[timeSlotId]
                        if (foundTime != null) {
                            println("🔍 TimeSelectionViewModel: ✅ Encontrado en timeSlotIdToTimeMap: $foundTime (timeSlotId=$timeSlotId)")
                            foundTime
                        } else {
                            // También intentar buscar en el timeSlotMap original
                            val foundInOriginal = _timeSlotMap.value.entries.find { it.value == timeSlotId }?.key
                            if (foundInOriginal != null) {
                                println("🔍 TimeSelectionViewModel: ✅ Encontrado en timeSlotMap original: $foundInOriginal")
                                foundInOriginal
                            } else {
                                println("🔍 TimeSelectionViewModel: ❌ No se pudo convertir ni encontrar en map para timeSlotId=$timeSlotId")
                                null
                            }
                        }
                    }
                }.toSet()
                
                // Combinar los horarios del backend con los que ya están marcados localmente
                val combinedBookedTimes = currentBookedTimes.apply {
                    addAll(bookedTimesFromBackend)
                }
                
                println("🔍 TimeSelectionViewModel: ✅ Final booked times (${combinedBookedTimes.size}): $combinedBookedTimes")
                println("🔍 TimeSelectionViewModel: - Del backend: ${bookedTimesFromBackend.size}")
                println("🔍 TimeSelectionViewModel: - Locales previos: ${currentBookedTimes.size}")
                println("🔍 TimeSelectionViewModel: TimeSlotMap tiene ${_timeSlotMap.value.size} entradas")
                _bookedTimeSlots.value = combinedBookedTimes
            }.onFailure { error ->
                println("🔍 TimeSelectionViewModel: ❌ Error loading reservations: ${error.message}")
                println("🔍 TimeSelectionViewModel: ⚠️ Manteniendo horarios reservados locales: ${currentBookedTimes.size}")
                _errorMessage.value = error.message
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
                // Asegurarse de que tenemos el horario correcto usando el timeSlotId como respaldo
                val timeToMark = selectedTime.ifEmpty {
                    _timeSlotIdToTimeMap.value[timeSlotId] ?: selectedTime
                }
                markTimeSlotAsBooked(timeToMark)
                println("🔍 TimeSelectionViewModel: ✅ Reserva creada exitosamente, horario marcado como reservado: $timeToMark (timeSlotId=$timeSlotId)")

                _timeSlotIdToTimeMap.value[timeSlotId]?.let { mappedTime ->
                    if (mappedTime != timeToMark) {
                        markTimeSlotAsBooked(mappedTime)
                        println("🔍 TimeSelectionViewModel: ✅ También marcado usando mapa inverso: $mappedTime")
                    }
                }

                persistBookedTimeSlotId(timeSlotId)

                refreshBookedTimeSlots(workerId, providerId)
                
                onDone(true, null)
            }.onFailure { error ->
                println("🔍 TimeSelectionViewModel: ❌ Error al crear reserva: ${error.message}")
                onDone(false, error.message)
            }
        }
    }
    
    private fun persistBookedTimeSlotId(timeSlotId: Long) {
        val bookedIds = getPersistedBookedTimeSlotIds().toMutableSet()
        bookedIds.add(timeSlotId)
        val idsString = bookedIds.joinToString(",")
        authPrefs.edit().putString("booked_time_slot_ids", idsString).apply()
        println("🔍 TimeSelectionViewModel: ✅ Guardado timeSlotId=$timeSlotId en SharedPreferences. Total: ${bookedIds.size}")
    }
    
    private fun getPersistedBookedTimeSlotIds(): Set<Long> {
        val idsString = authPrefs.getString("booked_time_slot_ids", "") ?: ""
        return if (idsString.isNotEmpty()) {
            idsString.split(",").mapNotNull { it.toLongOrNull() }.toSet()
        } else {
            emptySet()
        }
    }
    
    private fun loadPersistedBookedTimeSlots(timeSlotIdToTimeMap: Map<Long, String>) {
        val bookedIds = getPersistedBookedTimeSlotIds()
        if (bookedIds.isNotEmpty()) {
            println("🔍 TimeSelectionViewModel: Cargando ${bookedIds.size} timeSlotIds guardados localmente")
            val bookedTimes = bookedIds.mapNotNull { timeSlotId ->
                timeSlotIdToTimeMap[timeSlotId]?.also { time ->
                    println("🔍 TimeSelectionViewModel: ✅ Marcando horario reservado desde persistencia: $time (timeSlotId=$timeSlotId)")
                }
            }.toSet()
            
            if (bookedTimes.isNotEmpty()) {
                val currentBooked = _bookedTimeSlots.value.toMutableSet()
                currentBooked.addAll(bookedTimes)
                _bookedTimeSlots.value = currentBooked
                println("🔍 TimeSelectionViewModel: ✅ Horarios reservados cargados desde persistencia: $bookedTimes")
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
            println("🔍 TimeSelectionViewModel: Refrescando horarios ocupados para workerId=$workerId, providerId=$providerId")
            
            // Guardar el estado actual antes de intentar refrescar
            val currentBookedTimes = _bookedTimeSlots.value.toMutableSet()
            println("🔍 TimeSelectionViewModel: Horarios reservados actuales antes de refrescar: $currentBookedTimes")
            
            val reservations = reservationRepository.getAllDetails()
            reservations.onSuccess { details ->
                println("🔍 TimeSelectionViewModel: Cargadas ${details.size} reservas del backend")
                
                // Filtrar reservas del workerId (y providerId si está disponible)
                val filteredReservations = details.filter { reservation ->
                    val matchesWorker = reservation.workerId.id == workerId
                    val matchesProvider = providerId == null || reservation.provider.id == providerId
                    matchesWorker && matchesProvider
                }
                
                println("🔍 TimeSelectionViewModel: ${filteredReservations.size} reservas filtradas")
                
                // Convertir horarios a formato "hh:mm a"
                val bookedTimesFromBackend = filteredReservations.mapNotNull { reservation ->
                    val timeString = reservation.timeSlot.startTime
                    val timeSlotId = reservation.timeSlot.id
                    println("🔍 TimeSelectionViewModel: Procesando - timeSlotId=$timeSlotId, startTime=$timeString")
                    
                    val parsed = parseTime(timeString)
                    val formatted = parsed?.let { outputFormat.format(it) }
                    
                    if (formatted != null) {
                        println("🔍 TimeSelectionViewModel: ✅ Convertido: $timeString -> $formatted")
                        formatted
                    } else {
                        // Si no se puede parsear, usar el timeSlotIdToTimeMap como respaldo
                        val foundTime = _timeSlotIdToTimeMap.value[timeSlotId]
                        if (foundTime != null) {
                            println("🔍 TimeSelectionViewModel: ✅ Encontrado en timeSlotIdToTimeMap: $foundTime")
                            foundTime
                        } else {
                            // También intentar buscar en el timeSlotMap original
                            val foundInOriginal = _timeSlotMap.value.entries.find { it.value == timeSlotId }?.key
                            if (foundInOriginal != null) {
                                println("🔍 TimeSelectionViewModel: ✅ Encontrado en timeSlotMap original: $foundInOriginal")
                                foundInOriginal
                            } else {
                                println("🔍 TimeSelectionViewModel: ❌ No se pudo convertir ni encontrar en map")
                                null
                            }
                        }
                    }
                }.toSet()
                
                // Combinar los horarios del backend con los locales
                val combinedBookedTimes = currentBookedTimes.apply {
                    addAll(bookedTimesFromBackend)
                }
                
                println("🔍 TimeSelectionViewModel: Horarios ocupados actualizados (${combinedBookedTimes.size}): $combinedBookedTimes")
                _bookedTimeSlots.value = combinedBookedTimes
            }.onFailure { error ->
                println("🔍 TimeSelectionViewModel: ❌ Error al actualizar horarios ocupados: ${error.message}")
                // NO sobrescribir el estado local si falla - mantener los horarios locales
                println("🔍 TimeSelectionViewModel: ⚠️ Manteniendo horarios reservados locales: ${currentBookedTimes.size}")
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


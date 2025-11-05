package com.paxtech.mobileapp.features.clientDashboard.presentation.timeselection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import androidx.hilt.navigation.compose.hiltViewModel
import com.paxtech.mobileapp.features.clientDashboard.presentation.timeselection.TimeSelectionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class TimeSlot(
    val time: String,
    val status: TimeStatus = TimeStatus.AVAILABLE
)

enum class TimeStatus { AVAILABLE, SELECTED, BOOKED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectionScreen(
    serviceName: String,
    servicePrice: String,
    serviceDuration: Int,
    selectedProfessional: String,
    clientId: Long,
    providerId: Long,
    workerId: Long,
    salonName: String = "Glow & Go Hair Studio",
    salonAddress: String = "Av. Primavera 123, Santiago de Surco, Lima – Perú",
    onBack: () -> Unit,
    onContinue: (selectedDate: String, selectedTime: String, formattedDate: String, formattedTime: String) -> Unit,
    viewModel: TimeSelectionViewModel = hiltViewModel()
) {
    val localeEn = Locale.ENGLISH
    val localeEs = Locale("es", "ES")

    var currentWeekStart by remember { mutableStateOf(startOfWeek(Calendar.getInstance())) }
    var selectedDate by remember { mutableStateOf(cloneCal(currentWeekStart)) }
    var selectedTime by remember { mutableStateOf("10:00 AM") }

    val weekDates = remember(currentWeekStart.timeInMillis) {
        List(6) { idx -> addDays(currentWeekStart, idx) }
    }

    val monthTitle = remember(currentWeekStart.timeInMillis) {
        capFirst(SimpleDateFormat("MMMM yyyy", localeEn).format(currentWeekStart.time), localeEn)
    }

    // Horarios hardcodeados (no se toman del backend)
    val timeSlots = listOf(
        listOf("10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM"),
        listOf("11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM"),
        listOf("12:00 PM", "12:15 PM", "12:30 PM", "12:45 PM"),
        listOf("01:00 PM", "01:15 PM", "01:30 PM", "01:45 PM"),
        listOf("02:00 PM", "02:15 PM", "02:30 PM", "02:45 PM"),
        listOf("03:00 PM", "03:15 PM", "03:30 PM", "03:45 PM")
    )

    // Cargar reservas del backend para marcar horarios ocupados
    LaunchedEffect(Unit) { viewModel.load(workerId, providerId) }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val bookedTimeSlots by viewModel.bookedTimeSlots.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Select Date & Time",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF2F1FF)
                )
            )
        },
        containerColor = Color(0xFFF2F1FF)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Banner morado con esquinas redondeadas superiores
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(PrimaryPurple)
                        .padding(24.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    currentWeekStart = startOfWeek(addDays(currentWeekStart, -7))
                                    selectedDate = cloneCal(currentWeekStart)
                                }
                            ) {
                                Icon(
                                    Icons.Filled.ChevronLeft,
                                    contentDescription = "Previous",
                                    tint = Color.White
                                )
                            }
                            Text(
                                monthTitle,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(
                                onClick = {
                                    currentWeekStart = startOfWeek(addDays(currentWeekStart, +7))
                                    selectedDate = cloneCal(currentWeekStart)
                                }
                            ) {
                                Icon(
                                    Icons.Filled.ChevronRight,
                                    contentDescription = "Next",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            weekDates.forEach { dateCal ->
                                DateCircle(
                                    cal = dateCal,
                                    isSelected = isSameDay(dateCal, selectedDate),
                                    onSelect = { selectedDate = cloneCal(dateCal) }
                                )
                            }
                        }
                    }
                }

                // Sección blanca superpuesta
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-24).dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                            .padding(bottom = 90.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Time",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                LegendItem(color = PrimaryPurple, label = "Selected")
                                LegendItem(color = Color(0xFFF3EDFF), label = "Available")
                                LegendItem(color = Color(0xFFE7F1ED), label = "Booked")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mostrar horarios hardcodeados en filas de 4
                        timeSlots.forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { time ->
                                    val status = when {
                                        time == selectedTime -> TimeStatus.SELECTED
                                        bookedTimeSlots.contains(time) -> TimeStatus.BOOKED
                                        else -> TimeStatus.AVAILABLE
                                    }
                                    TimeSlotButton(
                                        time = time,
                                        status = status,
                                        onSelect = {
                                            if (status != TimeStatus.BOOKED) selectedTime = time
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BOTTOM BAR
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = {
                            val sdfEs = SimpleDateFormat("EEE d 'de' MMMM", localeEs)
                            val formatted = capFirst(sdfEs.format(selectedDate.time), localeEs)
                            
                            // Navegar primero para no bloquear la UI
                            onContinue(
                                selectedDate.get(Calendar.DAY_OF_MONTH).toString(),
                                selectedTime,
                                formatted,
                                selectedTime
                            )
                            
                            // Intentar crear la reserva en background (no bloquea navegación)
                            val selectedId = viewModel.getTimeSlotId(selectedTime)
                            if (selectedId != null) {
                                viewModel.createReservation(clientId, providerId, workerId, selectedId) { ok, error ->
                                    if (ok) {
                                        println("🔍 TimeSelectionScreen: Reserva creada exitosamente")
                                    } else {
                                        println("🔍 TimeSelectionScreen: Error al crear reserva: $error")
                                        // Aquí podrías mostrar un snackbar o mensaje si lo necesitas
                                    }
                                }
                            } else {
                                println("🔍 TimeSelectionScreen: No se encontró timeSlotId para $selectedTime")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            "Continue",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateCircle(
    cal: Calendar,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val dayNumber = cal.get(Calendar.DAY_OF_MONTH)
    val dayName = SimpleDateFormat("EEE", Locale.ENGLISH).format(cal.time).replace(".", "")

    Box(
        modifier = Modifier
            .width(50.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(if (isSelected) Color.White else Color(0x4DFFFFFF))
            .clickable { onSelect() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) PrimaryPurple else Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayNumber.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dayName,
                fontSize = 11.sp,
                color = if (isSelected) PrimaryPurple else Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun TimeSlotButton(
    time: String,
    status: TimeStatus,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        TimeStatus.SELECTED -> PrimaryPurple
        TimeStatus.BOOKED -> Color(0xFFE7F1ED)
        TimeStatus.AVAILABLE -> Color(0xFFF3EDFF)
    }
    val textColor = when (status) {
        TimeStatus.SELECTED -> Color.White
        TimeStatus.BOOKED -> Color(0xFF4DD0E1)
        TimeStatus.AVAILABLE -> Color(0xFF9CA3AF)
    }

    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(enabled = status != TimeStatus.BOOKED) { onSelect() },
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = time,
                fontSize = 13.sp,
                fontWeight = if (status == TimeStatus.SELECTED) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(text = label, fontSize = 10.sp, color = Color.Gray)
    }
}

private fun cloneCal(src: Calendar): Calendar =
    (src.clone() as Calendar)

private fun addDays(base: Calendar, days: Int): Calendar =
    (base.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, days) }

private fun startOfWeek(input: Calendar): Calendar {
    val c = input.clone() as Calendar
    c.firstDayOfWeek = Calendar.MONDAY
    while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        c.add(Calendar.DAY_OF_MONTH, -1)
    }
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    return c
}

private fun isSameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

private fun capFirst(s: String, locale: Locale): String =
    s.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }


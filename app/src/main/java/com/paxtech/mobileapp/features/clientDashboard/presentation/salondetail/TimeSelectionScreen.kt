package com.paxtech.mobileapp.features.clientDashboard.presentation.timeselection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TimeSlot(
    val time: String,
    val isAvailable: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectionScreen(
    serviceName: String,
    servicePrice: String,
    serviceDuration: Int,
    selectedProfessional: String,
    salonName: String = "Glow & Go Hair Studio",
    salonAddress: String = "Av. Primavera 123, Santiago de Surco, Lima – Perú",
    onBack: () -> Unit,
    onContinue: (selectedDate: String, selectedTime: String, formattedDate: String, formattedTime: String) -> Unit
) {
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    val october2025Dates = listOf(
        "6 Lun", "7 Mar", "8 Mié", "9 Jue", "10 Vie", "11 Sáb", "12 Dom"
    )

    val timeSlots = listOf(
        TimeSlot("10:00 a.m."),
        TimeSlot("10:30 a.m."),
        TimeSlot("11:00 a.m."),
        TimeSlot("11:30 a.m."),
        TimeSlot("12:00 p.m."),
        TimeSlot("12:30 p.m."),
        TimeSlot("13:00 p.m."),
        TimeSlot("13:30 p.m."),
        TimeSlot("14:00 p.m."),
        TimeSlot("14:30 p.m."),
        TimeSlot("15:00 p.m.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Seleccionar hora",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            servicePrice,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$serviceName - $serviceDuration min",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            selectedProfessional,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        if (selectedDate != null && selectedTime != null) {
                            Text(
                                "${selectedDate!!.split(" ")[0]} de octubre - ${selectedTime}",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (selectedDate != null && selectedTime != null) {
                                val formattedDate = "Martes ${selectedDate!!.split(" ")[0]} de octubre"
                                val formattedTime = "${selectedTime!!} – ${calculateEndTime(selectedTime!!, serviceDuration)}"
                                onContinue(selectedDate!!, selectedTime!!, formattedDate, formattedTime)
                            }
                        },
                        enabled = selectedDate != null && selectedTime != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8DEF8),
                            disabledContainerColor = Color(0xFFE8DEF8).copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "Continuar",
                            color = Color(0xFF6750A4),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(
                            text = selectedProfessional,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 14.sp
                        )
                    }

                    IconButton(onClick = { /* TODO: Abrir calendario */ }) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Calendario",
                            tint = Color.Black
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                ) {
                    Text(
                        "Octubre 2025",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(october2025Dates) { date ->
                            DateChip(
                                date = date,
                                isSelected = selectedDate == date,
                                onSelect = { selectedDate = date }
                            )
                        }
                    }
                }
            }

            items(timeSlots) { timeSlot ->
                TimeSlotCard(
                    timeSlot = timeSlot,
                    isSelected = selectedTime == timeSlot.time,
                    onSelect = { selectedTime = timeSlot.time }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun DateChip(
    date: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val parts = date.split(" ")
    val dayNumber = parts[0]
    val dayName = parts[1]

    Surface(
        modifier = Modifier
            .size(48.dp)
            .clickable { onSelect() },
        shape = CircleShape,
        color = if (isSelected) Color(0xFFE8DEF8) else Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayNumber,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF6750A4) else Color.Black
            )
            Text(
                text = dayName,
                fontSize = 10.sp,
                color = if (isSelected) Color(0xFF6750A4) else Color.Gray
            )
        }
    }
}

@Composable
private fun TimeSlotCard(
    timeSlot: TimeSlot,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(
                enabled = timeSlot.isAvailable,
                onClick = onSelect
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFE8DEF8) else Color.White,
        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null,
        tonalElevation = if (isSelected) 0.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = timeSlot.time,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) Color(0xFF6750A4) else Color.Black
            )
        }
    }
}

private fun calculateEndTime(startTime: String, duration: Int): String {
    return when (startTime) {
        "10:00 a.m." -> "10:50 a.m."
        "10:30 a.m." -> "11:20 a.m."
        "11:00 a.m." -> "11:50 a.m."
        "11:30 a.m." -> "12:20 p.m."
        "12:00 p.m." -> "12:50 p.m."
        "12:30 p.m." -> "13:20 p.m."
        "13:00 p.m." -> "13:50 p.m."
        "13:30 p.m." -> "14:20 p.m."
        "14:00 p.m." -> "14:50 p.m."
        "14:30 p.m." -> "15:20 p.m."
        "15:00 p.m." -> "15:50 p.m."
        else -> "${duration} min después"
    }
}
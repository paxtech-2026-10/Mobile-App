package com.paxtech.mobileapp.features.clientDashboard.presentation.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReservationDetailsDto
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    reservation: ReservationDetailsDto,
    formatDate: (String) -> String,
    formatTimeRange: (String, String) -> String,
    onBack: () -> Unit,
    onReservationCancelled: () -> Unit = {},
    viewModel: ReservationDetailViewModel = hiltViewModel(key = "reservation_${reservation.id}")
) {
    val salon by viewModel.salon.collectAsState()
    val salonAddress by viewModel.salonAddress.collectAsState()
    val isCancelling by viewModel.isCancelling.collectAsState()
    val cancelResult by viewModel.cancelResult.collectAsState()
    
    // Resetear estado cuando cambia la reservación
    LaunchedEffect(reservation.id) {
        // Resetear todo el estado del ViewModel para la nueva reservación
        viewModel.resetState()
        // Cargar información del salón
        viewModel.loadSalonInfo(reservation.provider.id.toInt())
    }
    
    // Manejar resultado de cancelación - solo una vez
    LaunchedEffect(cancelResult) {
        cancelResult?.onSuccess {
            // Resetear el resultado después de usarlo
            viewModel.resetCancelResult()
            onReservationCancelled()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundWhite
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundWhite)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con logo del salón
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Logo/Imagen del salón
                AsyncImage(
                    model = salon?.coverImageUrl ?: "",
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryPurple.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = reservation.provider.companyName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Cita confirmada
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Cita confirmada",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Duración: ${reservation.serviceId.duration} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Resumen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Resumen",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Servicio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = reservation.serviceId.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = "s/${reservation.serviceId.price}.00",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Fecha y Hora
                    Text(
                        text = formatDate(reservation.timeSlot.startTime),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = formatTimeRange(reservation.timeSlot.startTime, reservation.timeSlot.endTime),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Divider
                    HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "s/${reservation.serviceId.price}.00",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Política de cancelación
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Política de cancelación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cancela gratis en cualquier momento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información del establecimiento
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Información del establecimiento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = reservation.provider.companyName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
                if (salonAddress != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = salonAddress ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cancelar cita
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                TextButton(
                    onClick = { viewModel.cancelReservation(reservation.id) },
                    enabled = !isCancelling,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = PrimaryPurple
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isCancelling) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = PrimaryPurple,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCancelling) "Cancelando..." else "Cancelar cita",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryPurple
                    )
                }
                
                // Mostrar error si hay
                cancelResult?.onFailure { exception ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Error al cancelar: ${exception.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color(0xFFD32F2F)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

fun formatTimeRange(startTime: String, endTime: String): String {
    return try {
        val start = parseDate(startTime)
        val end = parseDate(endTime)
        
        if (start != null && end != null) {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            "${timeFormat.format(start)} - ${timeFormat.format(end)}"
        } else {
            "$startTime - $endTime"
        }
    } catch (e: Exception) {
        "$startTime - $endTime"
    }
}

private fun parseDate(dateString: String): Date? {
    return try {
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


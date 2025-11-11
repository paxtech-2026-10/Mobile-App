package com.paxtech.mobileapp.features.clientDashboard.presentation.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.AccessTime
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
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsScreen(
    viewModel: ReservationsViewModel = hiltViewModel(),
    onReservationClick: (ReservationDetailsDto) -> Unit = {}
) {
    val reservations by viewModel.reservations.collectAsState()
    // val selectedFilter by viewModel.selectedFilter.collectAsState() // Comentado por ahora
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // val filteredReservations = viewModel.getFilteredReservations() // Comentado por ahora
    val filteredReservations = reservations // Mostrar todas las reservaciones
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Appointment Booking",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
        ) {
            // Filter Tabs - Comentado por ahora
            /* FilterTabs(
                selectedFilter = selectedFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            ) */
            
            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryPurple)
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = error ?: "Error loading reservations",
                                color = TextSecondary
                            )
                            Button(
                                onClick = { viewModel.loadReservations() },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                filteredReservations.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No reservations found",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredReservations) { reservation ->
                            ReservationCard(
                                reservation = reservation,
                                formatDate = { viewModel.formatDate(it) },
                                onClick = { onReservationClick(reservation) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterTabs(
    selectedFilter: ReservationFilter,
    onFilterSelected: (ReservationFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReservationFilter.values().forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.name.replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryPurple,
                    containerColor = BackgroundWhite,
                    selectedLabelColor = BackgroundWhite,
                    labelColor = TextSecondary
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ReservationCard(
    reservation: ReservationDetailsDto,
    formatDate: (String) -> String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image placeholder (you can replace with actual salon image)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Service Name
                Text(
                    text = reservation.serviceId.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                // Date and Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary
                    )
                    Text(
                        text = formatDate(reservation.timeSlot.startTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                // Provider Name
                Text(
                    text = reservation.provider.companyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                
                // Status Buttons
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val status = getReservationStatus(reservation)
                    StatusChip(status = status)
                    
                    if (status == ReservationStatus.COMPLETED) {
                        TextButton(
                            onClick = { /* TODO: Rebook functionality */ },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = PrimaryPurple
                            )
                        ) {
                            Text("Rebook", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

enum class ReservationStatus {
    UPCOMING, COMPLETED, PENDING
}

@Composable
fun StatusChip(status: ReservationStatus) {
    val (text, color) = when (status) {
        ReservationStatus.UPCOMING -> "Upcoming" to androidx.compose.ui.graphics.Color(0xFF2196F3)
        ReservationStatus.COMPLETED -> "Completed" to androidx.compose.ui.graphics.Color(0xFF4CAF50)
        ReservationStatus.PENDING -> "Pending" to androidx.compose.ui.graphics.Color(0xFFE91E63)
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

fun getReservationStatus(reservation: ReservationDetailsDto): ReservationStatus {
    val now = Date()
    val startTime = parseDate(reservation.timeSlot.startTime)
    val endTime = parseDate(reservation.timeSlot.endTime)
    
    return when {
        endTime != null && endTime.before(now) -> ReservationStatus.COMPLETED
        !reservation.timeSlot.status -> ReservationStatus.PENDING
        startTime != null && startTime.after(now) -> ReservationStatus.UPCOMING
        else -> ReservationStatus.PENDING
    }
}

private fun parseDate(dateString: String): java.util.Date? {
    return try {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX"
        )
        
        for (format in formats) {
            try {
                val sdf = java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
                sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
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


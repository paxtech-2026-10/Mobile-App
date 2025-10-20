package com.paxtech.mobileapp.features.clientDashboard.presentation.salondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.AboutUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ReviewUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi
import com.paxtech.mobileapp.shared.model.Salon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonDetailScreen(
    salon: Salon?,
    services: List<ServiceUi>,
    reviews: List<ReviewUi>,
    about: AboutUi,
    onBack: () -> Unit,
    onReserveService: (ServiceUi) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Servicios", "Reseña", "Acerca de")

    var selectedCategory by remember { mutableStateOf("Destacados") }
    val categories = listOf("Destacados", "Cabello", "Barba", "Skin Care", "Prom")

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header con imagen
            item {
                Box {
                    AsyncImage(
                        model = salon?.coverImageUrl ?: "",
                        contentDescription = salon?.companyName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Botón de Back
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 40.dp, start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Badge de rating
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 40.dp, end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFA500),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("4.7", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "100 reviews",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Info del salón
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            salon?.companyName ?: "Glow & Go Hair Studio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.Gray
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Place,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            about.address,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Instagram", fontSize = 12.sp) }
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text("TikTok", fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { index, title ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            TextButton(onClick = { selectedTab = index }) {
                                Text(
                                    title,
                                    color = if (selectedTab == index) Color.Black else Color.Gray,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                            if (selectedTab == index) {
                                Box(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(2.dp)
                                        .background(Color.Black)
                                )
                            }
                        }
                    }
                }
            }

            // Contenido según tab
            when (selectedTab) {
                0 -> {
                    // Categorías
                    item {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories) { category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category },
                                    label = { Text(category, fontSize = 13.sp) }
                                )
                            }
                        }
                    }

                    // Servicios
                    items(services) { svc ->
                        ServiceCard(svc, onReserveService)
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
                1 -> {
                    items(reviews) { rev ->
                        ReviewRow(rev)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
                2 -> {
                    item {
                        AboutBlock(about)
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    svc: ServiceUi,
    onReserveService: (ServiceUi) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        svc.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        svc.subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            svc.price,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "${svc.durationMins} mins",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Button(
                        onClick = { onReserveService(svc) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8DEF8)
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Reservar",
                            color = Color(0xFF6750A4),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewRow(rev: ReviewUi) {
    Column(Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(rev.author, fontWeight = FontWeight.SemiBold)
            Row {
                repeat(rev.rating) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFA500),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(rev.comment, fontSize = 14.sp, color = Color.DarkGray)
    }
}

@Composable
private fun AboutBlock(about: AboutUi) {
    Column(Modifier.padding(16.dp)) {
        Text("Sobre nosotros", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Text(about.description, fontSize = 14.sp, color = Color.DarkGray)

        Spacer(Modifier.height(16.dp))
        Text("Horario", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(4.dp))
        about.schedule.forEach {
            Text("• $it", fontSize = 14.sp, color = Color.DarkGray)
        }

        Spacer(Modifier.height(16.dp))
        Text("Ubicación", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(4.dp))
        Text(about.address, fontSize = 14.sp, color = Color.DarkGray)

        Spacer(Modifier.height(16.dp))
        Text("Teléfono", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(4.dp))
        Text(about.phone, fontSize = 14.sp, color = Color.DarkGray)
    }
}
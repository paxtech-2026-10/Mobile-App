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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.AboutUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ReviewUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi
import com.paxtech.mobileapp.shared.model.Salon
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.DividerGray
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary

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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header con imagen
            item {
                Box {
                    AsyncImage(
                        model = salon?.coverImageUrl.orEmpty(),
                        contentDescription = salon?.companyName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Botón Back (blanco sobre imagen)
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 40.dp, start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = BackgroundWhite
                        )
                    }

                    // Badge de rating
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = BackgroundWhite,
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
                                tint = androidx.compose.ui.graphics.Color(0xFFFFA500),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("4.7", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(Modifier.width(6.dp))
                            Text("100 reviews", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // Info del salón
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundWhite)
                        .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            salon?.companyName ?: "Salón",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = TextSecondary
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Place,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            about.ubicacion,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Instagram", fontSize = 12.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = PrimaryPurple
                            )
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text("TikTok", fontSize = 12.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = PrimaryPurple
                            )
                        )
                    }
                }
            }

            // Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundWhite)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { index, title ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            TextButton(
                                onClick = { selectedTab = index },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = if (selectedTab == index) PrimaryPurple else TextSecondary
                                )
                            ) {
                                Text(
                                    title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                            if (selectedTab == index) {
                                Box(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(2.dp)
                                        .background(PrimaryPurple)
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
                                .background(BackgroundWhite),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories) { category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category },
                                    label = {
                                        Text(
                                            category,
                                            fontSize = 13.sp,
                                            color = if (selectedCategory == category) PrimaryPurple else TextSecondary
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = BackgroundWhite,
                                        selectedContainerColor = androidx.compose.ui.graphics.Color(0xFFEDE9FE), // morado muy claro
                                        labelColor = TextSecondary,
                                        selectedLabelColor = PrimaryPurple
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selectedCategory == category,
                                        borderColor = if (selectedCategory == category) PrimaryPurple else DividerGray
                                    )
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
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = DividerGray
                        )
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

/* ---------- Composables PRIVADOS ---------- */

@Composable
private fun ServiceCard(
    svc: ServiceUi,
    onReserveService: (ServiceUi) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
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
                    Text(svc.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                    Text(svc.subtitle, fontSize = 12.sp, color = TextSecondary)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(svc.price, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                        Text("${svc.durationMins} mins", fontSize = 11.sp, color = TextSecondary)
                    }
                    Button(
                        onClick = { onReserveService(svc) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Reservar",
                            color = BackgroundWhite,
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
            Text(rev.author, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Row {
                repeat(rev.rating) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color(0xFFFFA500),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(rev.comment, fontSize = 14.sp, color = TextSecondary)
    }
}

@Composable
private fun AboutBlock(about: AboutUi) {
    Column(Modifier.padding(16.dp)) {
        Text("Sobre nosotros", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(about.email, fontSize = 14.sp, color = TextSecondary)

        Spacer(Modifier.height(16.dp))
        Text("Horario", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        about.socials.forEach { Text("• $it", fontSize = 14.sp, color = TextSecondary) }

        Spacer(Modifier.height(16.dp))
        Text("Ubicación", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(about.ubicacion, fontSize = 14.sp, color = TextSecondary)
    }
}

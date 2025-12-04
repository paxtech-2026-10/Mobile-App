package com.paxtech.mobileapp.features.services.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa // Icono placeholder
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.paxtech.mobileapp.features.services.domain.Service
import com.paxtech.mobileapp.ui.theme.BackgroundGray
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.DividerGray
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary

@Preview(showBackground = true)
@Composable
fun SearchServiceView(
    viewModel: SearchServiceViewModel = hiltViewModel(),
    onReserveService: (Int) -> Unit = {}
) {
    val categories = listOf(
        "Maquillaje", "Manicure", "Barberia", "Cuidado Facial", "Masajes",
        "Coloracion", "Corte", "Peinados", "Alisados",
        "Hidratacion", "Pestañas", "Cejas"
    )

    val query = viewModel.query.collectAsState()
    val services = viewModel.services.collectAsState()
    val isSearchBarActive = remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp)
        ) {
            // --- HEADER ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (!isSearchBarActive.value) {
                    Text(
                        text = "Explora servicios",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¿Qué tratamiento buscas hoy?",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // --- SEARCH BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSearchBarActive.value) {
                    IconButton(onClick = { isSearchBarActive.value = false }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                }

                OutlinedTextField(
                    value = query.value,
                    onValueChange = { viewModel.onChangeQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej. Corte de cabello", color = TextSecondary) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = if (isSearchBarActive.value) PrimaryPurple else TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = DividerGray,
                        focusedContainerColor = BackgroundGray.copy(alpha = 0.5f),
                        unfocusedContainerColor = DividerGray,
                        cursorColor = PrimaryPurple
                    ),
                    singleLine = true,
                    // Activar búsqueda al hacer click
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        .also { interactionSource ->
                            androidx.compose.runtime.LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                        isSearchBarActive.value = true
                                        viewModel.searchService()
                                    }
                                }
                            }
                        }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- CONTENT ---
            if (!isSearchBarActive.value) {
                // VISTA DE CATEGORÍAS (Grid limpio)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Categorías Populares",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories) { category ->
                            CategoryCard(
                                title = category,
                                onClick = {
                                    viewModel.onChangeQuery(category)
                                    viewModel.searchService()
                                    isSearchBarActive.value = true
                                }
                            )
                        }
                    }
                }
            } else {
                // VISTA DE RESULTADOS (Lista limpia)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Resultados (${services.value.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(services.value) { service ->
                            ServiceCard(
                                service = service,
                                onReserveClick = { onReserveService(service.providerId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .height(70.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BackgroundGray),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp) // Flat styling like modern apps
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ServiceCard(
    service: Service,
    onReserveClick: () -> Unit
) {
    // Estilo tipo "PopularSalonCard" (Horizontal)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReserveClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. IMAGEN (Placeholder)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundGray),
                contentAlignment = Alignment.Center
            ) {
                // Si tienes URL de imagen, usa AsyncImage aquí.
                // Por ahora usamos un icono estilizado.
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = PrimaryPurple.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. INFORMACIÓN
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Salón (Simulado como ubicación/nombre)
                Text(
                    text = "Salón #${service.providerId}", // Idealmente esto sería el nombre del salón
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    maxLines = 1
                )

                // Duración
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${service.duration} min",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Precio y Botón alineados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "S/ ${service.price}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )
                    )

                    Button(
                        onClick = onReserveClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "Reservar",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

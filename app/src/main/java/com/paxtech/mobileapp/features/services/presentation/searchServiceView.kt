package com.paxtech.mobileapp.features.services.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.paxtech.mobileapp.features.services.domain.Service
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextLight
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary

/** Categoría de servicio con su emoji e imagen temática. */
private data class Category(val label: String, val emoji: String, val keyword: String)

private val serviceCategories = listOf(
    Category("Maquillaje", "💄", "makeup"),
    Category("Manicure", "💅", "manicure,nails"),
    Category("Barbería", "🧔", "barber"),
    Category("Cuidado Facial", "🧖", "facial,skincare"),
    Category("Masajes", "💆", "massage,spa"),
    Category("Coloración & Mechas", "🎨", "haircolor,salon"),
    Category("Corte de Cabello", "✂️", "haircut"),
    Category("Peinados", "💇", "hairstyle"),
    Category("Alisados", "🪮", "hair,straight"),
    Category("Hidratación & Mascarillas", "🧴", "spa,facemask"),
    Category("Permanentes", "🌀", "curls,hair"),
    Category("Pestañas & Cejas", "👁️", "eyelashes,eyebrows"),
)

/** Imagen temática determinista (loremflickr por keyword + seed estable). */
private fun themedImageUrl(keyword: String, seed: String): String =
    "https://loremflickr.com/seed/${seed}/600/400/${keyword}"

/** Degradado de marca que se ve detrás de la imagen (mientras carga o si falla). */
private val brandGradient = Brush.linearGradient(
    listOf(PrimaryPurple.copy(alpha = 0.85f), Color(0xFFB0179B))
)

@Composable
fun SearchServiceView(
    viewModel: SearchServiceViewModel = hiltViewModel(),
    onReserveService: (Int) -> Unit = {} // providerId del servicio
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var isSearchBarActive by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    fun runSearch(term: String) {
        if (term.isNotBlank()) {
            viewModel.onChangeQuery(term)
            viewModel.searchService()
            isSearchBarActive = true
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
    ) {
        // Encabezado
        if (!isSearchBarActive) {
            Text(
                text = "Descubre",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "Encuentra el servicio perfecto para ti",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )
        }

        // Barra de búsqueda
        SearchBar(
            query = query,
            onQueryChange = viewModel::onChangeQuery,
            onSearch = { runSearch(query) },
            onClear = {
                viewModel.onChangeQuery("")
                isSearchBarActive = false
            },
            showBack = isSearchBarActive,
            onBack = {
                isSearchBarActive = false
                focusManager.clearFocus()
            }
        )

        Spacer(Modifier.height(24.dp))

        if (!isSearchBarActive) {
            SectionTitle("Categorías")
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(serviceCategories) { category ->
                    CategoryCard(category = category, onClick = { runSearch(category.label) })
                }
            }
        } else {
            SectionTitle("Resultados", count = results.size)
            Spacer(Modifier.height(12.dp))
            when {
                isLoading -> LoadingState()
                results.isEmpty() -> EmptyState(query = query)
                else -> LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(results) { result ->
                        ServiceCard(
                            result = result,
                            onReserveClick = { onReserveService(result.service.providerId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    showBack: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showBack) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = "Volver",
                    tint = TextPrimary
                )
            }
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            shape = RoundedCornerShape(27.dp),
            color = PrimaryPurple.copy(alpha = 0.06f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 18.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Busca un servicio o categoría",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextLight
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                        cursorBrush = SolidColor(PrimaryPurple),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Limpiar",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, count: Int? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        if (count != null) {
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = PrimaryPurple.copy(alpha = 0.10f)
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryPurple,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(category: Category, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(brandGradient)
            .clickable(onClick = onClick)
    ) {
        // Imagen temática (encima del degradado de marca)
        AsyncImage(
            model = themedImageUrl(category.keyword, seed = category.label.lowercase()),
            contentDescription = category.label,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Scrim para legibilidad del texto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                    )
                )
        )
        // Emoji badge
        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(34.dp)
                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Text(text = category.emoji, fontSize = 17.sp)
        }
        // Label
        Text(
            text = category.label,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        )
    }
}

/** Deriva un keyword de imagen a partir del nombre del servicio (con fallback genérico). */
private fun keywordForService(name: String): String {
    val n = name.lowercase()
    return serviceCategories.firstOrNull { cat ->
        n.contains(cat.label.lowercase().substringBefore(" "))
    }?.keyword ?: "salon,beauty,spa"
}

@Composable
private fun ServiceCard(result: ServiceSearchResult, onReserveClick: () -> Unit) {
    val service = result.service
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = BackgroundWhite,
        shadowElevation = 3.dp
    ) {
        Column {
            // Banner de imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(brandGradient)
            ) {
                AsyncImage(
                    model = themedImageUrl(keywordForService(service.name), seed = "svc${service.id}"),
                    contentDescription = service.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(Modifier.height(8.dp))
                // Salón que ofrece el servicio
                InfoRow(icon = Icons.Default.Store, text = result.salonName, emphasize = true)
                Spacer(Modifier.height(4.dp))
                // Dirección del salón + distancia al usuario
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = result.salonLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (result.distanceText != null) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = PrimaryPurple.copy(alpha = 0.10f)
                        ) {
                            Text(
                                text = result.distanceText,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryPurple,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${service.duration} min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PrimaryPurple.copy(alpha = 0.10f)
                    ) {
                        Text(
                            text = "S/ ${service.price}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryPurple,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onReserveClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        contentColor = BackgroundWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reservar ahora", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    emphasize: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (emphasize) PrimaryPurple else TextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = if (emphasize)
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            else MaterialTheme.typography.bodySmall,
            color = if (emphasize) TextPrimary else TextSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryPurple)
    }
}

@Composable
private fun EmptyState(query: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            tint = TextLight,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Sin resultados",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (query.isBlank()) "Escribe algo para buscar" else "No encontramos servicios para \"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

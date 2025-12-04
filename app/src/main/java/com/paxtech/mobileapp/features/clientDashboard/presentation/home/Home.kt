package com.paxtech.mobileapp.features.clientDashboard.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.paxtech.mobileapp.shared.model.Salon
import com.paxtech.mobileapp.features.clientDashboard.domain.model.RatingSummary
import com.paxtech.mobileapp.features.clientDashboard.domain.model.Discount
import com.paxtech.mobileapp.features.clientDashboard.domain.model.DiscountType
import com.paxtech.mobileapp.core.utils.LocationUtils
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.BackgroundGray
import com.paxtech.mobileapp.ui.theme.DividerGray
import com.paxtech.mobileapp.R
import kotlinx.coroutines.delay

@Composable
fun Home(
    viewModel: HomeViewModel = hiltViewModel(),
    onSalonClick: (Int) -> Unit = {}
) {
    val recommendedSalons by viewModel.recommendedSalons.collectAsState()
    val favoriteSalons by viewModel.favoriteSalons.collectAsState()
    val recentVisits by viewModel.recentVisits.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val profileImageUrl by viewModel.profileImageUrl.collectAsState()

    // Estados del buscador
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    // Ratings de salones
    val salonRatings by viewModel.salonRatings.collectAsState()

    // Salones con distancias calculadas
    val salonsWithDistance by viewModel.salonsWithDistance.collectAsState()

    // Direcciones de los salones
    val salonAddresses by viewModel.salonAddresses.collectAsState()

    // Ubicación del usuario
    val userLocation by viewModel.userLocation.collectAsState()
    val userAddress by viewModel.userAddress.collectAsState()
    val hasLocationPermission by viewModel.hasLocationPermission.collectAsState()

    // Estados de descuentos
    val discounts by viewModel.discounts.collectAsState()
    val isLoadingDiscounts by viewModel.isLoadingDiscounts.collectAsState()

    val isHomeLoading by viewModel.isLoadingHome.collectAsState()

    // Estado local para el TextField
    var searchText by remember { mutableStateOf("") }

    // Launcher para solicitar permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.reloadLocationAndDistances()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
        // Asegurar que el nombre se carga cuando se muestra la pantalla
        viewModel.loadUserName()
    }

    // Sincronizar el texto del TextField con el ViewModel
    LaunchedEffect(searchText) {
        viewModel.updateSearchQuery(searchText)
    }

    // Calcular iniciales del nombre del usuario
    val userInitials = remember(userName) {
        userName.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").uppercase().take(2)
    }

    if (isHomeLoading) {
        HomeLoadingSkeleton()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile picture
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PrimaryPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!profileImageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = profileImageUrl,
                                    contentDescription = "Profile image",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = userInitials,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryPurple
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // User info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val currentLocation = userLocation
                            if (hasLocationPermission && currentLocation != null) {
                                // Mostrar dirección si está disponible, sino mostrar coordenadas como fallback
                                Text(
                                    text = if (userAddress != null) {
                                        "📍 $userAddress"
                                    } else {
                                        "📍 ${
                                            String.format(
                                                "%.6f",
                                                currentLocation.latitude
                                            )
                                        }, ${String.format("%.6f", currentLocation.longitude)}"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text(
                                    text = "Permitir acceso a ubicación",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    modifier = Modifier.clickable {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        /* Notifications
                    Box {
                        IconButton(onClick = { /* TODO: Navigate to notifications */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = TextPrimary
                            )
                        }
                        // Notification dot
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF44336))
                        )
                    }*/
                    }
                }
            }

            // Search Bar con Dropdown
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .zIndex(1f) // Asegurar que el dropdown esté por encima
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search TextField
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            TextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                placeholder = {
                                    Text(
                                        text = "Buscar salones...",
                                        color = TextSecondary
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Buscar",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (searchText.isNotEmpty()) {
                                        IconButton(onClick = {
                                            searchText = ""
                                            viewModel.clearSearch()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Limpiar",
                                                tint = TextSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = BackgroundGray,
                                    unfocusedContainerColor = BackgroundGray,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            // Dropdown de resultados
                            if (searchResults.isNotEmpty() && searchText.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 60.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = BackgroundWhite
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        searchResults.take(5).forEach { salon ->
                                            SearchResultItem(
                                                salon = salon,
                                                onClick = {
                                                    viewModel.saveVisit(salon)
                                                    viewModel.clearSearch()
                                                    searchText = ""
                                                    onSalonClick(salon.id)
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            } else if (isSearching && searchText.length >= 2) {
                                // Mostrar indicador de carga
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 60.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = BackgroundWhite
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Buscando...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        /* Filter button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filtro",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }*/
                    }
                }
            }

            // Promotional Carousel - Descuentos dinámicos
            item {
                if (discounts.isNotEmpty()) {
                    DiscountCarousel(
                        discounts = discounts,
                        onDiscountClick = { discount ->
                            // Guardar visita y navegar al salón del descuento
                            viewModel.saveVisit(
                                recommendedSalons.find { it.id == discount.providerProfileId }
                                    ?: return@DiscountCarousel
                            )
                            onSalonClick(discount.providerProfileId)
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else if (isLoadingDiscounts) {
                    // Mostrar loading placeholder
                    DiscountCarouselPlaceholder()
                }
            }

            // Categories Section - COMENTADO: Sección de categorías deshabilitada temporalmente
            /*
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Ver todo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* TODO: Navigate to all categories */ }
                )
            }
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(getCategories()) { category ->
                    CategoryItem(category = category)
                }
            }
        }
        */

            // Nearby Salons Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Salones Cercanos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    // COMENTADO: Botón "Ver todo" deshabilitado temporalmente
                    /*
                Text(
                    text = "Ver todo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* TODO: Navigate to all salons */ }
                )
                */
                }
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(salonsWithDistance) { (salon, distance) ->
                        NearbySalonCard(
                            salon = salon,
                            onClick = {
                                viewModel.saveVisit(salon)
                                onSalonClick(salon.id)
                            },
                            isFavorite = favoriteSalons.any { it.id == salon.id },
                            onFavoriteClick = { viewModel.toggleFavorite(salon) },
                            ratingSummary = salonRatings[salon.id],
                            distanceKm = distance,
                            address = salonAddresses[salon.id]
                        )
                    }
                }
            }

            // Recent Salons Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Salones Recientes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    // COMENTADO: Botón "Ver todo" deshabilitado temporalmente
                    /*
                Text(
                    text = "Ver todo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* TODO: Navigate to all salons */ }
                )
                */
                }
            }
            items(recentVisits.ifEmpty { recommendedSalons.take(3) }) { salon ->
                // Buscar la distancia del salón en la lista ordenada
                val distance = salonsWithDistance.find { it.first.id == salon.id }?.second
                PopularSalonCard(
                    salon = salon,
                    onClick = { onSalonClick(salon.id) },
                    isFavorite = favoriteSalons.any { it.id == salon.id },
                    onFavoriteClick = { viewModel.toggleFavorite(salon) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    ratingSummary = salonRatings[salon.id],
                    distanceKm = distance,
                    address = salonAddresses[salon.id]
                )
            }

            // Favorite Salons Section
            if (favoriteSalons.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Salones Favoritos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
                items(favoriteSalons) { salon ->
                    // Buscar la distancia del salón en la lista ordenada
                    val distance = salonsWithDistance.find { it.first.id == salon.id }?.second
                    PopularSalonCard(
                        salon = salon,
                        onClick = { onSalonClick(salon.id) },
                        isFavorite = true,
                        onFavoriteClick = { viewModel.toggleFavorite(salon) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        ratingSummary = salonRatings[salon.id],
                        distanceKm = distance,
                        address = salonAddresses[salon.id]
                    )
                }
            }

        }
    }
}

@Composable
fun HomeLoadingSkeleton() {
    val shimmerAlpha by rememberInfiniteTransition(label = "homeSkeleton")
        .animateFloat(
            initialValue = 0.3f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "homeSkeletonAlpha"
        )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        HeaderSkeleton(shimmerAlpha = shimmerAlpha)

        SearchSkeleton(shimmerAlpha = shimmerAlpha)

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitleSkeleton(width = 180.dp, shimmerAlpha = shimmerAlpha)
        DiscountBannerSkeleton(shimmerAlpha = shimmerAlpha)

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitleSkeleton(width = 160.dp, shimmerAlpha = shimmerAlpha)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                NearbyVerticalCardSkeleton(shimmerAlpha = shimmerAlpha)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitleSkeleton(width = 170.dp, shimmerAlpha = shimmerAlpha)
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                RecentHorizontalCardSkeleton(shimmerAlpha = shimmerAlpha)
            }
        }
    }
}


@Composable
private fun HeaderSkeleton(shimmerAlpha: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(DividerGray.copy(alpha = shimmerAlpha))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DividerGray.copy(alpha = shimmerAlpha))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DividerGray.copy(alpha = shimmerAlpha))
            )
        }
    }
}

@Composable
private fun SearchSkeleton(shimmerAlpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DividerGray.copy(alpha = shimmerAlpha * 0.5f))
    )
}

@Composable
private fun SectionTitleSkeleton(width: Dp, shimmerAlpha: Float) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .width(width)
            .height(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(DividerGray.copy(alpha = shimmerAlpha))
    )
}

@Composable
private fun DiscountBannerSkeleton(shimmerAlpha: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DividerGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {

    }
}

@Composable
private fun NearbyVerticalCardSkeleton(shimmerAlpha: Float) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DividerGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.Black.copy(alpha = 0.05f))
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.width(100.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(Color.Black.copy(alpha = 0.05f)))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.8f).height(12.dp).clip(RoundedCornerShape(4.dp)).background(Color.Black.copy(alpha = 0.05f)))

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.05f))
                )
            }
        }
    }
}

@Composable
private fun RecentHorizontalCardSkeleton(shimmerAlpha: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DividerGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.05f))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.width(100.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(Color.Black.copy(alpha = 0.05f)))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.9f).height(12.dp).clip(RoundedCornerShape(4.dp)).background(Color.Black.copy(alpha = 0.05f)))
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.05f))
                )
            }
        }
    }
}

// Category data class
data class Category(
    val name: String,
    val icon: String,
    val color: Color
)

@Composable
fun CategoryItem(category: Category) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(category.color),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for icon - you can replace with actual icons
            Text(
                text = category.icon,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

fun getCategories(): List<Category> = listOf(
    Category("Corte de Cabello", "✂️", Color(0xFFFFB6C1)),
    Category("Afeitado", "🪒", Color(0xFFE1BEE7)),
    Category("Maquillaje", "💄", Color(0xFFFFB74D)),
    Category("Manicure", "💅", Color(0xFFE1BEE7)),
    Category("Peinado", "💇", Color(0xFFB2DFDB))
)

@Composable
fun NearbySalonCard(
    salon: Salon,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    ratingSummary: RatingSummary? = null,
    distanceKm: Float? = null,
    address: String? = null
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image with favorite button
            Box {
                AsyncImage(
                    model = salon.coverImageUrl,
                    contentDescription = salon.companyName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (isFavorite) Color(0xFFF44336) else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = salon.companyName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (distanceKm != null && distanceKm != Float.MAX_VALUE) {
                            "→ ${LocationUtils.formatDistance(distanceKm)}"
                        } else {
                            "→ ? km"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = address ?: LocationUtils.extractAddress(salon.location),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Calificación",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (ratingSummary != null) {
                            "%.1f (%d)".format(ratingSummary.averageRating, ratingSummary.reviewCount)
                        } else {
                            "Sin calificar"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Reservar Ahora",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun PopularSalonCard(
    salon: Salon,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    ratingSummary: RatingSummary? = null,
    distanceKm: Float? = null,
    address: String? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image on the left
            Box {
                AsyncImage(
                    model = salon.coverImageUrl,
                    contentDescription = salon.companyName,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (isFavorite) Color(0xFFF44336) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Information on the right
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = salon.companyName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (distanceKm != null && distanceKm != Float.MAX_VALUE) {
                            "→ ${LocationUtils.formatDistance(distanceKm)}"
                        } else {
                            "→ ? km"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = address ?: LocationUtils.extractAddress(salon.location),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Calificación",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (ratingSummary != null) {
                            "%.1f (%d)".format(ratingSummary.averageRating, ratingSummary.reviewCount)
                        } else {
                            "Sin calificar"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Reservar Ahora",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Componente para cada item del dropdown
@Composable
fun SearchResultItem(
    salon: Salon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono de salón
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = PrimaryPurple,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Información del salón
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = salon.companyName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = LocationUtils.extractAddress(salon.location),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ========== COMPONENTES DEL CAROUSEL DE DESCUENTOS ==========

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscountCarousel(
    discounts: List<Discount>,
    onDiscountClick: (Discount) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { discounts.size })
    
    // Auto-scroll cada 5 segundos
    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % discounts.size
            pagerState.animateScrollToPage(nextPage)
        }
    }
    
    Column(modifier = modifier) {
        // Título de sección
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ofertas Especiales",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        
        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { page ->
            DiscountCard(
                discount = discounts[page],
                onClick = { onDiscountClick(discounts[page]) }
            )
        }
        
        // Indicadores de página
        if (discounts.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(discounts.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) 
                                    PrimaryPurple 
                                else 
                                    PrimaryPurple.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun DiscountCard(
    discount: Discount,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryPurple
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Mostrar el valor del descuento
                Text(
                    text = when (discount.discountType) {
                        DiscountType.PERCENTAGE -> "${discount.discountValue}% Descuento"
                        DiscountType.FIXED -> "S/${discount.discountValue} Descuento"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Título del descuento
                Text(
                    text = discount.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Subtítulo
                Text(
                    text = discount.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryPurple
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Ver Salón",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun DiscountCarouselPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundGray
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Cargando ofertas...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

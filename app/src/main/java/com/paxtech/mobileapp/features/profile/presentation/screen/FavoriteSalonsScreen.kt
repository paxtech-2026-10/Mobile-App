package com.paxtech.mobileapp.features.profile.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paxtech.mobileapp.features.profile.presentation.model.FavoriteSalonUi
import com.paxtech.mobileapp.features.profile.presentation.model.FavoriteSalonsUiState
import com.paxtech.mobileapp.ui.theme.BackgroundGray
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.LightPurple
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteSalonsScreen(
    state: FavoriteSalonsUiState,
    onBack: () -> Unit,
    onBookNow: (FavoriteSalonUi) -> Unit,
    onRemoveFavorite: (FavoriteSalonUi) -> Unit,
    onDismissRemoval: () -> Unit,
    onConfirmRemoval: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingRemoval = state.pendingRemoval

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundWhite,
        topBar = {
            TopAppBar(
                title = { Text(text = "Salones favoritos", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundWhite,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            }

            !state.hasFavorites -> {
                EmptyFavoriteSalons(modifier = Modifier.padding(innerPadding))
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (state.errorMessage != null) {
                        item {
                            FavoriteErrorMessage(
                                message = state.errorMessage,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    items(state.salons, key = { it.id }) { salon ->
                        FavoriteSalonCard(
                            salon = salon,
                            onBookNow = { onBookNow(salon) },
                            onRemove = { onRemoveFavorite(salon) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }

    if (pendingRemoval != null) {
        AlertDialog(
            onDismissRequest = onDismissRemoval,
            confirmButton = {
                TextButton(onClick = onConfirmRemoval) {
                    Text(text = "Sí", color = PrimaryPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRemoval) {
                    Text(text = "No", color = TextSecondary)
                }
            },
            title = {
                Text(
                    text = "¿Estás seguro?",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Quitarás a ${pendingRemoval.name} de tu lista de favoritos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        )
    }
}

@Composable
private fun FavoriteSalonCard(
    salon: FavoriteSalonUi,
    onBookNow: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SalonThumbnail(imageUrl = salon.imageUrl)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = salon.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = salon.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RatingBadge(rating = salon.formattedRating, reviews = salon.reviewsCount)
                        DistanceBadge(distance = salon.formattedDistance)
                    }
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = PrimaryPurple
                    )
                }
            }
            Button(
                onClick = onBookNow,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    contentColor = BackgroundWhite
                )
            ) {
                Text(text = "Reservar ahora")
            }
        }
    }
}

@Composable
private fun FavoriteErrorMessage(message: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun SalonThumbnail(imageUrl: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    listOf(PrimaryPurple.copy(alpha = 0.15f), LightPurple.copy(alpha = 0.2f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun RatingBadge(rating: String, reviews: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = PrimaryPurple,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$rating (${reviews})",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary
        )
    }
}

@Composable
private fun DistanceBadge(distance: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Place,
            contentDescription = null,
            tint = PrimaryPurple,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = distance,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary
        )
    }
}

@Composable
private fun EmptyFavoriteSalons(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(PrimaryPurple.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Aún no tienes salones favoritos",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Explora y guarda tus salones preferidos para acceder más rápido a ellos.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun FavoriteSalonCardPreview() {
    val salon = FavoriteSalonUi(
        id = 1,
        name = "Braids & Layers",
        imageUrl = "",
        location = "2464 Royal Ln, Mesa, NV",
        email = "contact@braidslayers.com",
        socials = emptyList(),
        rating = 4.9,
        distanceKm = 3.5,
        reviewsCount = 136
    )
    FavoriteSalonCard(salon = salon, onBookNow = {}, onRemove = {})
}

@Preview(showBackground = true)
@Composable
private fun FavoriteSalonsScreenPreview() {
    MaterialTheme {
        FavoriteSalonsScreen(
            state = FavoriteSalonsUiState(
                isLoading = false,
                salons = listOf(
                    FavoriteSalonUi(
                        id = 1,
                        name = "Braids & Layers",
                        imageUrl = "",
                        location = "2464 Royal Ln, Mesa, NV",
                        email = "contact@braidslayers.com",
                        socials = emptyList(),
                        rating = 4.9,
                        distanceKm = 3.5,
                        reviewsCount = 136
                    )
                )
            ),
            onBack = {},
            onBookNow = {},
            onRemoveFavorite = {},
            onDismissRemoval = {},
            onConfirmRemoval = {}
        )
    }
}
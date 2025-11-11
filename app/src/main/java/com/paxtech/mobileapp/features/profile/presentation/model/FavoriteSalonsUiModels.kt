package com.paxtech.mobileapp.features.profile.presentation.model

import com.paxtech.mobileapp.shared.model.Salon
import java.util.Locale
import kotlin.math.abs

/**
 * UI model representing a favorite salon displayed in the profile module.
 */
data class FavoriteSalonUi(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val location: String,
    val email: String,
    val socials: Map<String, String>,
    val rating: Double,
    val distanceKm: Double,
    val reviewsCount: Int
) {
    val formattedRating: String
        get() = String.format(Locale.getDefault(), "%.1f", rating)

    val formattedDistance: String
        get() = String.format(Locale.getDefault(), "%.1f km", distanceKm)
}

/**
 * UI state for the favorite salons screen.
 */
data class FavoriteSalonsUiState(
    val isLoading: Boolean = true,
    val salons: List<FavoriteSalonUi> = emptyList(),
    val pendingRemoval: FavoriteSalonUi? = null,
    val errorMessage: String? = null
) {
    val hasFavorites: Boolean get() = salons.isNotEmpty()
}

/**
 * Maps a [Salon] from local storage into a [FavoriteSalonUi] ready for display.
 * Additional UI-only fields such as rating, distance and review count are
 * generated deterministically so the values remain stable between sessions.
 */
fun Salon.toFavoriteSalonUi(): FavoriteSalonUi {
    val normalizedSeed = abs(id.hashCode())
    val rating = 4.0 + (normalizedSeed % 10) / 10.0
    val distance = 0.6 + (normalizedSeed % 90) / 10.0
    val reviews = 30 + (normalizedSeed % 120)

    return FavoriteSalonUi(
        id = id,
        name = companyName,
        imageUrl = coverImageUrl,
        location = location,
        email = email,
        socials = socials,
        rating = rating,
        distanceKm = distance,
        reviewsCount = reviews
    )
}

fun FavoriteSalonUi.toSalon(): Salon = Salon(
    id = id,
    companyName = name,
    coverImageUrl = imageUrl,
    location = location,
    email = email,
    socials = socials
)
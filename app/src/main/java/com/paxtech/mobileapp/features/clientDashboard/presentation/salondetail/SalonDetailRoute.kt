package com.paxtech.mobileapp.features.clientDashboard.presentation.salondetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi

@Composable
fun SalonDetailRoute(
    salonId: Int,
    onBack: () -> Unit,
    onReserveService: (
        service: ServiceUi,
        salonName: String,
        salonAddress: String,
        salonRating: Double,
        salonImageUrl: String
    ) -> Unit,
    // si no estás usando Hilt aquí, puedes crear el VM por defecto:
    viewModel: SalonDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(salonId) { viewModel.load(salonId) }

    val salon by viewModel.salon.collectAsState()
    val services by viewModel.services.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val about by viewModel.about.collectAsState()

    val defaultServices = if (services.isEmpty())
        listOf(ServiceUi("0", "Servicio no disponible", "Descripción no disponible", "s/0.00", 0))
    else services

    val defaultReviews = if (reviews.isEmpty())
        listOf(com.paxtech.mobileapp.features.clientDashboard.presentation.details.ReviewUi("Cliente", 5, "Sin reseñas"))
    else reviews

    SalonDetailScreen(
        salon = salon,
        services = defaultServices,
        reviews = defaultReviews,
        about = about,
        onBack = onBack,
        onReserveService = { service ->
            onReserveService(
                service,
                salon?.companyName ?: "Salón",
                about.ubicacion,
                4.7,
                salon?.coverImageUrl.orEmpty()
            )
        }
    )
}

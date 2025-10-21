package com.paxtech.mobileapp.features.clientDashboard.presentation.salondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.AboutUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ReviewUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.home.HomeMockProvider
import com.paxtech.mobileapp.shared.model.Salon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SalonDetailViewModel : ViewModel() {

    private val _salon = MutableStateFlow<Salon?>(null)
    val salon: StateFlow<Salon?> = _salon

    private val _services = MutableStateFlow<List<ServiceUi>>(emptyList())
    val services: StateFlow<List<ServiceUi>> = _services

    private val _reviews = MutableStateFlow<List<ReviewUi>>(emptyList())
    val reviews: StateFlow<List<ReviewUi>> = _reviews

    private val _about = MutableStateFlow(
        AboutUi(
            description = "Servicios profesionales de belleza.",
            schedule = listOf("Lun - Sáb: 9:00 - 19:00"),
            address = "Av. Primavera 123, Santiago de Surco",   // <-- dirección fija, NO 'Cargando'
            phone = "+51 987 654 321"
        )
    )
    val about: StateFlow<AboutUi> = _about

    fun load(salonId: Int) {
        viewModelScope.launch {
            // Toma el salón EXACTO desde los mocks de Home
            _salon.value = HomeMockProvider.getById(salonId)

            // mocks de servicios/reseñas
            _services.value = listOf(
                ServiceUi("1", "Corte simple", "Corte básico", "s/40.00", 50),
                ServiceUi("2", "Corte + Lavado", "Corte con lavado", "s/60.00", 75),
                ServiceUi("3", "Coloración", "Tinte completo", "s/120.00", 120)
            )
            _reviews.value = listOf(
                ReviewUi("María González", 5, "Excelente servicio"),
                ReviewUi("Carlos López", 4, "Muy buena atención")
            )
        }
    }
}

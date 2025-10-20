package com.paxtech.mobileapp.features.clientDashboard.presentation.salondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ReviewUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.AboutUi
import com.paxtech.mobileapp.shared.model.Salon

class SalonDetailViewModel : ViewModel() {

    private val _salon = MutableStateFlow<Salon?>(null)
    val salon: StateFlow<Salon?> = _salon

    private val _services = MutableStateFlow<List<ServiceUi>>(emptyList())
    val services: StateFlow<List<ServiceUi>> = _services

    private val _reviews = MutableStateFlow<List<ReviewUi>>(emptyList())
    val reviews: StateFlow<List<ReviewUi>> = _reviews

    private val _about = MutableStateFlow<AboutUi>(
        AboutUi(
            description = "Cargando información...",
            schedule = listOf("Cargando horario..."),
            address = "Cargando dirección...",
            phone = "Cargando teléfono..."
        )
    )
    val about: StateFlow<AboutUi> = _about

    fun load(salonId: Int) {
        viewModelScope.launch {
            _salon.value = Salon(
                id = salonId,
                companyName = "Salón de Belleza",
                coverImageUrl = "https://images.unsplash.com/photo-1562322140-8baeececf3df?q=80&w=1000"
            )

            _services.value = listOf(
                ServiceUi("1", "Corte simple", "Corte de cabello básico", "s/40.00", 50),
                ServiceUi("2", "Corte + Lavado", "Corte completo con lavado", "s/60.00", 75),
                ServiceUi("3", "Coloración", "Tinte completo del cabello", "s/120.00", 120)
            )

            _reviews.value = listOf(
                ReviewUi("María González", 5, "Excelente servicio, muy profesionales"),
                ReviewUi("Carlos López", 4, "Buen atención, volveré pronto")
            )

            _about.value = AboutUi(
                description = "Salón de belleza especializado en cortes modernos y tratamientos capilares.",
                schedule = listOf("Lunes a Viernes: 9:00 - 19:00", "Sábados: 9:00 - 17:00"),
                address = "Av. Primavera 123, Santiago de Surco",
                phone = "+51 987 654 321"
            )
        }
    }
}
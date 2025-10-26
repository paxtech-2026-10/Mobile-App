package com.paxtech.mobileapp.features.clientDashboard.presentation.salondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.clientDashboard.domain.domain.SalonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ReviewUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.AboutUi
import com.paxtech.mobileapp.features.services.domain.ServiceRepository
import com.paxtech.mobileapp.shared.model.Salon
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SalonDetailViewModel @Inject constructor(private val salonRepository: SalonRepository, private val serviceRepository: ServiceRepository) : ViewModel() {

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
            try {
                // Cargar datos del salón desde el API
                val salonData = salonRepository.getSalonById(salonId)

                if (salonData != null) {
                    _salon.value = salonData

                    // Actualizar la información "About" con datos reales del salón
                    _about.value = AboutUi(
                        description = "Salón de belleza especializado en cortes modernos y tratamientos capilares.",
                        schedule = listOf("Lunes a Viernes: 9:00 - 19:00", "Sábados: 9:00 - 17:00"),
                        address = salonData.companyName,
                        phone = "+51 987 654 321"
                    )
                } else {
                    // Si no se encuentra el salón en el API, usar datos mockeados
                    _salon.value = Salon(
                        id = salonId,
                        companyName = "Salón $salonId",
                        coverImageUrl = "https://images.unsplash.com/photo-1562322140-8baeececf3df?q=80&w=1000"
                    )
                }

                /*
                _services.value = listOf(
                    ServiceUi("1", "Corte simple", "Corte de cabello básico", "s/40.00", 50),
                    ServiceUi("2", "Corte + Lavado", "Corte completo con lavado", "s/60.00", 75),
                    ServiceUi("3", "Coloración", "Tinte completo del cabello", "s/120.00", 120)
                )*/
                try {
                    val salonService = serviceRepository.getServiceByProviderId(salonId)
                    _services.value = salonService.map { service->
                        ServiceUi(
                            id = service.id.toString(),
                            title = service.name,
                            subtitle = "Servicio profesional",
                            price = "s/${service.price}.00",
                            durationMins = service.duration
                        )
                    }
                }catch (e: Exception) {
                    println("🔍 SalonDetailViewModel: Error loading services for salon $salonId: ${e.message}")
                    // Mantener servicios mockeados en caso de error
                    _services.value = listOf(
                        ServiceUi("1", "Servicio no disponible", "Error al cargar servicios", "s/0.00", 0)
                    )
                }

                _reviews.value = listOf(
                    ReviewUi("María González", 5, "Excelente servicio, muy profesionales"),
                    ReviewUi("Carlos López", 4, "Buen atención, volveré pronto")
                )

            } catch (e: Exception) {
                println("🔍 SalonDetailViewModel: Error loading salon $salonId: ${e.message}")
                // En caso de error, usar datos mockeados
                _salon.value = Salon(
                    id = salonId,
                    companyName = "Salón $salonId",
                    coverImageUrl = "https://images.unsplash.com/photo-1562322140-8baeececf3df?q=80&w=1000"
                )
            }
        }

    }
}
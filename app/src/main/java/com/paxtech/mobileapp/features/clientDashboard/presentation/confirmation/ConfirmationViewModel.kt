package com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReservationRepository
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateReservationRequest
import com.paxtech.mobileapp.features.payment.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ConfirmationUiState>(ConfirmationUiState.Idle)
    val uiState: StateFlow<ConfirmationUiState> = _uiState.asStateFlow()
    
    fun createReservationAndStartPayment(
        clientId: Long,
        providerId: Long,
        serviceId: Long,
        timeSlotId: Long,
        workerId: Long,
        amount: Double
    ) {
        viewModelScope.launch {
            _uiState.value = ConfirmationUiState.CreatingReservation
            
            // 1. Crear reservación
            val reservationResult = reservationRepository.createReservation(
                CreateReservationRequest(
                    clientId = clientId,
                    providerId = providerId,
                    serviceId = serviceId,
                    timeSlotId = timeSlotId,
                    workerId = workerId
                )
            )
            
            reservationResult.onSuccess { reservationResponse ->
                println("🔍 ConfirmationViewModel: Reservación creada exitosamente - ID: ${reservationResponse.id}")
                _uiState.value = ConfirmationUiState.ReservationCreated(reservationResponse.id)
                
                // 2. Crear pago
                _uiState.value = ConfirmationUiState.CreatingPayment
                val paymentResult = paymentRepository.createPayment(
                    amount = amount,
                    currency = "PEN",
                    reservationId = reservationResponse.id,
                    clientId = clientId
                )
                
                paymentResult.onSuccess { payment ->
                    println("🔍 ConfirmationViewModel: Pago creado exitosamente - ID: ${payment.id}")
                    _uiState.value = ConfirmationUiState.CreatingPaymentLink(payment.id)
                    
                    // 3. Crear payment link
                    val linkResult = paymentRepository.createPaymentLink(
                        paymentId = payment.id,
                        amount = payment.amount,
                        currency = payment.currency,
                        description = "Reserva de servicio"
                    )
                    
                    linkResult.onSuccess { paymentLinkUrl ->
                        println("🔍 ConfirmationViewModel: Payment link creado exitosamente")
                        _uiState.value = ConfirmationUiState.PaymentLinkReady(
                            reservationId = reservationResponse.id,
                            paymentId = payment.id,
                            paymentLinkUrl = paymentLinkUrl
                        )
                    }.onFailure { error ->
                        println("❌ ConfirmationViewModel: Error al crear payment link: ${error.message}")
                        _uiState.value = ConfirmationUiState.Error("Error al crear link de pago: ${error.message}")
                    }
                }.onFailure { error ->
                    println("❌ ConfirmationViewModel: Error al crear pago: ${error.message}")
                    _uiState.value = ConfirmationUiState.Error("Error al crear pago: ${error.message}")
                }
            }.onFailure { error ->
                println("❌ ConfirmationViewModel: Error al crear reservación: ${error.message}")
                _uiState.value = ConfirmationUiState.Error("Error al crear reservación: ${error.message}")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = ConfirmationUiState.Idle
    }
}

sealed class ConfirmationUiState {
    object Idle : ConfirmationUiState()
    object CreatingReservation : ConfirmationUiState()
    data class ReservationCreated(val reservationId: Long) : ConfirmationUiState()
    object CreatingPayment : ConfirmationUiState()
    data class CreatingPaymentLink(val paymentId: Long) : ConfirmationUiState()
    data class PaymentLinkReady(
        val reservationId: Long,
        val paymentId: Long,
        val paymentLinkUrl: String
    ) : ConfirmationUiState()
    data class Error(val message: String) : ConfirmationUiState()
}


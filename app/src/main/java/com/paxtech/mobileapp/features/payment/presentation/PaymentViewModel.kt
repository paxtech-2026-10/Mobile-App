package com.paxtech.mobileapp.features.payment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.payment.domain.models.Payment
import com.paxtech.mobileapp.features.payment.domain.models.PaymentStatus
import com.paxtech.mobileapp.features.payment.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()
    
    private var pollingJob: Job? = null
    
    fun startPaymentFlow(
        amount: Double,
        currency: String,
        reservationId: Long,
        clientId: Long,
        description: String = "Reserva de servicio"
    ) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.CreatingPayment
            
            // 1. Crear pago
            val paymentResult = paymentRepository.createPayment(amount, currency, reservationId, clientId)
            paymentResult.onSuccess { payment ->
                _paymentState.value = PaymentState.CreatingPaymentLink(payment.id)
                
                // 2. Crear payment link
                val linkResult = paymentRepository.createPaymentLink(
                    paymentId = payment.id,
                    amount = payment.amount,
                    currency = payment.currency,
                    description = description
                )
                
                linkResult.onSuccess { paymentLinkUrl ->
                    _paymentState.value = PaymentState.PaymentLinkReady(payment.id, paymentLinkUrl)
                }.onFailure { error ->
                    _paymentState.value = PaymentState.Error("Error al crear link de pago: ${error.message}")
                }
            }.onFailure { error ->
                _paymentState.value = PaymentState.Error("Error al crear pago: ${error.message}")
            }
        }
    }
    
    fun startPollingPaymentStatus(paymentId: Long) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            val maxAttempts = 60 // 5 minutos con polling cada 5 segundos
            var attempts = 0
            
            while (attempts < maxAttempts) {
                delay(5000) // 5 segundos
                attempts++
                
                val result = paymentRepository.getPaymentById(paymentId)
                result.onSuccess { payment ->
                    when (payment.paymentStatus) {
                        PaymentStatus.SUCCEEDED -> {
                            _paymentState.value = PaymentState.PaymentSucceeded(payment)
                            pollingJob?.cancel()
                            return@launch
                        }
                        PaymentStatus.FAILED -> {
                            _paymentState.value = PaymentState.PaymentFailed("El pago falló")
                            pollingJob?.cancel()
                            return@launch
                        }
                        PaymentStatus.PENDING -> {
                            _paymentState.value = PaymentState.PollingPayment(paymentId, attempts, maxAttempts)
                        }
                    }
                }.onFailure { error ->
                    _paymentState.value = PaymentState.Error("Error al verificar estado: ${error.message}")
                }
            }
            
            // Timeout después de 5 minutos
            _paymentState.value = PaymentState.PaymentTimeout("Tiempo de espera agotado")
        }
    }
    
    fun stopPolling() {
        pollingJob?.cancel()
    }
    
    /**
     * Verifica inmediatamente el estado del pago sin esperar el delay del polling
     * Útil cuando la app vuelve al foreground después de completar el pago
     */
    fun checkPaymentStatusImmediately(paymentId: Long) {
        viewModelScope.launch {
            val result = paymentRepository.getPaymentById(paymentId)
            result.onSuccess { payment ->
                when (payment.paymentStatus) {
                    PaymentStatus.SUCCEEDED -> {
                        println("🔍 PaymentViewModel: Payment succeeded detected immediately!")
                        _paymentState.value = PaymentState.PaymentSucceeded(payment)
                        pollingJob?.cancel()
                    }
                    PaymentStatus.FAILED -> {
                        println("🔍 PaymentViewModel: Payment failed detected immediately!")
                        _paymentState.value = PaymentState.PaymentFailed("El pago falló")
                        pollingJob?.cancel()
                    }
                    PaymentStatus.PENDING -> {
                        println("🔍 PaymentViewModel: Payment still pending, continuing polling")
                        // Si aún está pendiente, el polling continuará verificando
                    }
                }
            }.onFailure { error ->
                println("🔍 PaymentViewModel: Error checking payment status immediately: ${error.message}")
                // No cambiar el estado si hay error, dejar que el polling continúe
            }
        }
    }
    
    fun resetState() {
        _paymentState.value = PaymentState.Idle
    }
    
    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}

sealed class PaymentState {
    object Idle : PaymentState()
    object CreatingPayment : PaymentState()
    data class CreatingPaymentLink(val paymentId: Long) : PaymentState()
    data class PaymentLinkReady(val paymentId: Long, val paymentLinkUrl: String) : PaymentState()
    data class PollingPayment(val paymentId: Long, val currentAttempt: Int, val maxAttempts: Int) : PaymentState()
    data class PaymentSucceeded(val payment: Payment) : PaymentState()
    data class PaymentFailed(val message: String) : PaymentState()
    data class PaymentTimeout(val message: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}



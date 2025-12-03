package com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReservationRepository
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.CreateReservationRequest
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.DiscountRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.model.Discount
import com.paxtech.mobileapp.features.clientDashboard.domain.model.DiscountType
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
    private val paymentRepository: PaymentRepository,
    private val discountRepository: DiscountRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ConfirmationUiState>(ConfirmationUiState.Idle)
    val uiState: StateFlow<ConfirmationUiState> = _uiState.asStateFlow()
    
    // Estados para cupones
    private val _availableDiscounts = MutableStateFlow<List<Discount>>(emptyList())
    val availableDiscounts: StateFlow<List<Discount>> = _availableDiscounts.asStateFlow()
    
    private val _appliedDiscount = MutableStateFlow<Discount?>(null)
    val appliedDiscount: StateFlow<Discount?> = _appliedDiscount.asStateFlow()
    
    private val _couponError = MutableStateFlow<String?>(null)
    val couponError: StateFlow<String?> = _couponError.asStateFlow()
    
    init {
        loadDiscounts()
    }
    
    private fun loadDiscounts() {
        viewModelScope.launch {
            try {
                val discounts = discountRepository.getAllDiscounts()
                _availableDiscounts.value = discounts
                println("🎟️ ConfirmationViewModel: Loaded ${discounts.size} discounts")
            } catch (e: Exception) {
                println("🎟️ ConfirmationViewModel: Error loading discounts: ${e.message}")
                _availableDiscounts.value = emptyList()
            }
        }
    }
    
    /**
     * Valida y aplica un cupón basado en el título del descuento
     * El título debe coincidir exactamente con el título del descuento
     * @param couponTitle Título del descuento a buscar (debe coincidir exactamente)
     * @param providerId ID del salón/proveedor para validar que el descuento pertenezca a este salón
     */
    fun applyCoupon(couponTitle: String, providerId: Long) {
        _couponError.value = null
        
        if (couponTitle.isBlank()) {
            _couponError.value = "Por favor ingresa un cupón"
            _appliedDiscount.value = null
            return
        }
        
        // Buscar descuento por título exacto (case-sensitive)
        val discount = _availableDiscounts.value.find { 
            it.title == couponTitle 
        }
        
        if (discount == null) {
            _couponError.value = "Cupón no válido"
            _appliedDiscount.value = null
            return
        }
        
        // Validar que el descuento pertenezca al salón actual
        if (discount.providerProfileId.toLong() != providerId) {
            _couponError.value = "Este cupón no es válido para este salón"
            _appliedDiscount.value = null
            return
        }
        
        // Aplicar descuento
        _appliedDiscount.value = discount
        _couponError.value = null
        println("🎟️ ConfirmationViewModel: Cupón aplicado: ${discount.title}")
    }
    
    /**
     * Remueve el cupón aplicado
     */
    fun removeCoupon() {
        _appliedDiscount.value = null
        _couponError.value = null
    }
    
    /**
     * Calcula el precio final con descuento aplicado
     * @param basePrice Precio base del servicio
     * @return Precio final después de aplicar el descuento
     */
    fun calculateFinalPrice(basePrice: Double): Double {
        val discount = _appliedDiscount.value ?: return basePrice
        
        return when (discount.discountType) {
            DiscountType.PERCENTAGE -> {
                val discountAmount = basePrice * (discount.discountValue / 100.0)
                (basePrice - discountAmount).coerceAtLeast(0.0)
            }
            DiscountType.FIXED -> {
                (basePrice - discount.discountValue).coerceAtLeast(0.0)
            }
        }
    }
    
    /**
     * Calcula el monto del descuento aplicado
     * @param basePrice Precio base del servicio
     * @return Monto del descuento
     */
    fun calculateDiscountAmount(basePrice: Double): Double {
        val discount = _appliedDiscount.value ?: return 0.0
        
        return when (discount.discountType) {
            DiscountType.PERCENTAGE -> {
                basePrice * (discount.discountValue / 100.0)
            }
            DiscountType.FIXED -> {
                discount.discountValue.toDouble()
            }
        }
    }
    
    fun createReservationAndStartPayment(
        clientId: Long,
        providerId: Long,
        serviceId: Long,
        timeSlotId: Long,
        workerId: Long,
        baseAmount: Double
    ) {
        viewModelScope.launch {
            _uiState.value = ConfirmationUiState.CreatingReservation
            
            // Calcular monto final con descuento aplicado
            val finalAmount = calculateFinalPrice(baseAmount)
            
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
                
                // 2. Crear pago con el monto final (con descuento aplicado)
                _uiState.value = ConfirmationUiState.CreatingPayment
                val paymentResult = paymentRepository.createPayment(
                    amount = finalAmount,
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



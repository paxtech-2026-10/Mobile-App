package com.paxtech.mobileapp.features.payment.data.repository

import com.paxtech.mobileapp.features.payment.data.remote.models.PaymentDto
import com.paxtech.mobileapp.features.payment.data.remote.services.PaymentService
import com.paxtech.mobileapp.features.payment.domain.models.Payment
import com.paxtech.mobileapp.features.payment.domain.models.PaymentStatus
import com.paxtech.mobileapp.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val paymentService: PaymentService
) : PaymentRepository {
    
    override suspend fun createPayment(
        amount: Double,
        currency: String,
        reservationId: Long,
        clientId: Long
    ): Result<Payment> = withContext(Dispatchers.IO) {
        try {
            println("🔍 PaymentRepositoryImpl: Creating payment - amount: $amount, currency: $currency, reservationId: $reservationId, clientId: $clientId")
            val request = com.paxtech.mobileapp.features.payment.data.remote.models.CreatePaymentRequestDto(
                amount = amount,
                currency = currency,
                reservationId = reservationId,
                clientId = clientId
            )
            
            val response = paymentService.createPayment(request)
            
            if (response.isSuccessful) {
                val paymentDto = response.body()
                if (paymentDto != null) {
                    println("🔍 PaymentRepositoryImpl: Payment created successfully - ID: ${paymentDto.id}, Status: ${paymentDto.paymentStatus}")
                    Result.success(paymentDto.toDomain())
                } else {
                    println("🔍 PaymentRepositoryImpl: Payment response body is null")
                    Result.failure(Exception("Payment response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                println("🔍 PaymentRepositoryImpl: Create payment failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Create payment failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("🔍 PaymentRepositoryImpl: Exception creating payment: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    override suspend fun createPaymentLink(
        paymentId: Long,
        amount: Double,
        currency: String,
        description: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            println("🔍 PaymentRepositoryImpl: Creating payment link - paymentId: $paymentId, amount: $amount")
            val request = com.paxtech.mobileapp.features.payment.data.remote.models.CreatePaymentLinkRequestDto(
                paymentId = paymentId,
                amount = amount,
                currency = currency,
                description = description
            )
            
            val response = paymentService.createPaymentLink(request)
            
            if (response.isSuccessful) {
                val linkResponse = response.body()
                if (linkResponse != null) {
                    println("🔍 PaymentRepositoryImpl: Payment link created successfully: ${linkResponse.paymentLinkUrl}")
                    Result.success(linkResponse.paymentLinkUrl)
                } else {
                    println("🔍 PaymentRepositoryImpl: Payment link response body is null")
                    Result.failure(Exception("Payment link response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                println("🔍 PaymentRepositoryImpl: Create payment link failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Create payment link failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("🔍 PaymentRepositoryImpl: Exception creating payment link: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    override suspend fun getPaymentById(paymentId: Long): Result<Payment> = withContext(Dispatchers.IO) {
        try {
            println("🔍 PaymentRepositoryImpl: Getting payment by ID: $paymentId")
            val response = paymentService.getPaymentById(paymentId)
            
            if (response.isSuccessful) {
                val paymentDto = response.body()
                if (paymentDto != null) {
                    println("🔍 PaymentRepositoryImpl: Payment retrieved - ID: ${paymentDto.id}, Status: ${paymentDto.paymentStatus}")
                    Result.success(paymentDto.toDomain())
                } else {
                    println("🔍 PaymentRepositoryImpl: Payment response body is null")
                    Result.failure(Exception("Payment response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                println("🔍 PaymentRepositoryImpl: Get payment failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Get payment failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("🔍 PaymentRepositoryImpl: Exception getting payment: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

private fun PaymentDto.toDomain(): Payment {
    val status = when (paymentStatus.uppercase()) {
        "SUCCEEDED" -> PaymentStatus.SUCCEEDED
        "FAILED" -> PaymentStatus.FAILED
        else -> PaymentStatus.PENDING
    }
    
    return Payment(
        id = id,
        amount = amount,
        currency = currency,
        paymentStatus = status,
        stripePaymentLinkId = stripePaymentLinkId,
        stripeCheckoutSessionId = stripeCheckoutSessionId,
        reservationId = reservationId,
        clientId = clientId
    )
}


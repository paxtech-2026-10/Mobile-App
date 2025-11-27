package com.paxtech.mobileapp.features.clientDashboard.data.repository

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.DiscountService
import com.paxtech.mobileapp.features.clientDashboard.domain.model.Discount
import com.paxtech.mobileapp.features.clientDashboard.domain.model.DiscountType
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.DiscountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DiscountRepositoryImpl @Inject constructor(
    private val service: DiscountService
): DiscountRepository {
    override suspend fun getAllDiscounts(): List<Discount> = withContext(Dispatchers.IO) {
        try {
            println("🎟️ DiscountRepositoryImpl: Fetching discounts from API...")
            val response = service.getAllDiscounts()
            println("🎟️ DiscountRepositoryImpl: Response code: ${response.code()}")
            
            if (response.isSuccessful) {
                val body = response.body() ?: emptyList()
                println("🎟️ DiscountRepositoryImpl: Received ${body.size} discounts")
                
                val discounts = body.mapNotNull { dto ->
                    try {
                        Discount(
                            id = dto.id ?: return@mapNotNull null,
                            title = dto.title.orEmpty(),
                            subtitle = dto.subtitle.orEmpty(),
                            content = dto.content.orEmpty(),
                            discountType = DiscountType.fromString(dto.discountType),
                            discountValue = dto.discountValue ?: 0,
                            providerProfileId = dto.providerProfileId ?: 0
                        )
                    } catch (e: Exception) {
                        println("🎟️ DiscountRepositoryImpl: Error mapping discount: ${e.message}")
                        null
                    }
                }
                return@withContext discounts
            } else {
                println("🎟️ DiscountRepositoryImpl: Error response: ${response.errorBody()?.string()}")
            }
            emptyList()
        } catch (e: Exception) {
            println("🎟️ DiscountRepositoryImpl: Exception: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}


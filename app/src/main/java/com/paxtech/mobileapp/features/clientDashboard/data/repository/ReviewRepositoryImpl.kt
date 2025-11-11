package com.paxtech.mobileapp.features.clientDashboard.data.repository

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReviewService
import com.paxtech.mobileapp.features.clientDashboard.domain.model.RatingSummary
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.ReviewRepository
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ReviewUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewService: ReviewService
): ReviewRepository {
    
    override suspend fun getReviewsByProviderId(providerId: Int): List<ReviewUi> = withContext(Dispatchers.IO) {
        try {
            println("🔍 ReviewRepositoryImpl: Making API call for reviews...")
            val resp = reviewService.getAllReviews()
            println("🔍 ReviewRepositoryImpl: Response code: ${resp.code()}")
            println("🔍 ReviewRepositoryImpl: Response successful: ${resp.isSuccessful}")
            
            if (resp.isSuccessful) {
                val body = resp.body()
                println("🔍 ReviewRepositoryImpl: Raw body: $body")
                val bodyList = body ?: emptyList()
                println("🔍 ReviewRepositoryImpl: Body size: ${bodyList.size}")
                
                val reviews = bodyList
                    .filter { it.providerId == providerId }
                    .map { dto ->
                        ReviewUi(
                            author = "Cliente ${dto.clientId}",
                            rating = dto.rating,
                            comment = dto.review
                        )
                    }
                println("🔍 ReviewRepositoryImpl: Filtered reviews for provider $providerId: ${reviews.size}")
                return@withContext reviews
            } else {
                println("🔍 ReviewRepositoryImpl: Response not successful: ${resp.errorBody()?.string()}")
            }
            emptyList()
        } catch (e: Exception) {
            println("🔍 ReviewRepositoryImpl: Exception occurred: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
    
    override suspend fun getRatingSummary(providerId: Int): RatingSummary? = withContext(Dispatchers.IO) {
        try {
            println("🔍 ReviewRepositoryImpl: Getting rating summary for provider $providerId...")
            val resp = reviewService.getAllReviews()
            println("🔍 ReviewRepositoryImpl: Response code: ${resp.code()}")
            println("🔍 ReviewRepositoryImpl: Response successful: ${resp.isSuccessful}")
            
            if (resp.isSuccessful) {
                val bodyList = resp.body() ?: emptyList()
                val reviews = bodyList.filter { it.providerId == providerId }
                
                if (reviews.isEmpty()) {
                    println("🔍 ReviewRepositoryImpl: No reviews found for provider $providerId")
                    return@withContext null
                }
                
                val averageRating = reviews.map { it.rating }.average()
                val reviewCount = reviews.size
                
                println("🔍 ReviewRepositoryImpl: Rating summary for provider $providerId: avg=$averageRating, count=$reviewCount")
                
                return@withContext RatingSummary(
                    averageRating = averageRating,
                    reviewCount = reviewCount
                )
            } else {
                println("🔍 ReviewRepositoryImpl: Response not successful: ${resp.errorBody()?.string()}")
            }
            null
        } catch (e: Exception) {
            println("🔍 ReviewRepositoryImpl: Exception occurred getting rating summary: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}


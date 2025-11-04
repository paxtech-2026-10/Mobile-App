package com.paxtech.mobileapp.features.clientDashboard.data.repository

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.SalonService
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.SalonRepository
import com.paxtech.mobileapp.shared.model.Salon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SalonRepositoryImpl @Inject constructor(
    private val service: SalonService
): SalonRepository {
    override suspend fun getAllSalons(): List<Salon> = withContext(Dispatchers.IO){
        try {
            println("🔍 SalonRepositoryImpl: Making API call...")
            val resp = service.getAllSalons()
            println("🔍 SalonRepositoryImpl: Response code: ${resp.code()}")
            println("🔍 SalonRepositoryImpl: Response successful: ${resp.isSuccessful}")
            println("🔍 SalonRepositoryImpl: Response message: ${resp.message()}")
            
            if (resp.isSuccessful){
                val body = resp.body()
                println("🔍 SalonRepositoryImpl: Raw body: $body")
                val bodyList = body ?: emptyList()
                println("🔍 SalonRepositoryImpl: Body size: ${bodyList.size}")
                
                if (bodyList.isNotEmpty()) {
                    println("🔍 SalonRepositoryImpl: First item: ${bodyList.first()}")
                }
                
                val salons = bodyList.map { dto ->
                    val salon = Salon(
                        dto.id ?: 0,
                        dto.companyName.orEmpty(),
                        dto.coverImageUrl.orEmpty()
                    )
                    println("🔍 SalonRepositoryImpl: Mapped salon: $salon")
                    salon
                }
                println("🔍 SalonRepositoryImpl: Final salons list: $salons")
                return@withContext salons
            } else {
                println("🔍 SalonRepositoryImpl: Response not successful: ${resp.errorBody()?.string()}")
            }
            emptyList()
        } catch (e: Exception) {
            println("🔍 SalonRepositoryImpl: Exception occurred: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // SalonRepositoryImpl.kt
    override suspend fun getSalonById(id: Int): Salon? = withContext(Dispatchers.IO) {
        try {
            val resp = service.getSalonById(id)
            if (!resp.isSuccessful) return@withContext null
            resp.body()?.let { dto ->
                Salon(
                    id = dto.id ?: 0,
                    companyName = dto.companyName.orEmpty(),
                    coverImageUrl = dto.coverImageUrl.orEmpty()
                )
            }
        } catch (e: Exception) {
            null
        }
    }


}


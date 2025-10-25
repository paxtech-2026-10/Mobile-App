package com.paxtech.mobileapp.features.services.data.service

import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi
import com.paxtech.mobileapp.features.services.domain.Service
import com.paxtech.mobileapp.features.services.domain.ServiceRepository
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(private val service: ServiceService): ServiceRepository {
    override suspend fun searchService(query: String): List<Service> = withContext(Dispatchers.IO) {
        val response = service.getAllServices()
        if (response.isSuccessful) {
            response.body()?.let { servicesDto ->
                return@withContext servicesDto.map {
                    dto -> Service(
                        id = dto.id,
                        name = dto.name,
                        duration = dto.duration,
                        price = dto.price,
                        providerId = dto.providerId
                    )
                }.filter { it.name.contains(query, ignoreCase = true) }
            }
        }
        return@withContext emptyList()
    }

    override suspend fun getServiceByProviderId(query: Int): List<Service> = withContext(Dispatchers.IO) {
        val response = service.getAllServices()
        if (response.isSuccessful){
            response.body()?.let { servicesDto ->
                return@withContext servicesDto.map { dto ->
                    Service(
                    id = dto.id,
                    name = dto.name,
                    duration = dto.duration,
                    price = dto.price,
                    providerId = dto.providerId
                )
                }.filter { it.providerId == query }
            }
        }
        return@withContext emptyList()
    }


}
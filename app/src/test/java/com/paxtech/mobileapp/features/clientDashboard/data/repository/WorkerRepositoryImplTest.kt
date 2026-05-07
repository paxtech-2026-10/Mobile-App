package com.paxtech.mobileapp.features.clientDashboard.data.repository

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.WorkerDto
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.WorkerService
import com.paxtech.mobileapp.features.clientDashboard.domain.models.Worker
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorkerRepositoryImplTest {

    @Test
    fun getAllWorkersWhenResponseIsSuccessful() = runBlocking {
        val dtos = listOf(
            WorkerDto(
                id = 1L,
                name = "Ana Lopez",
                specialization = "Maquillista",
                photoUrl = "https://example.com/ana.jpg",
                providerId = 10L
            ),
            WorkerDto(
                id = 2L,
                name = "Luis Perez",
                specialization = null,
                photoUrl = null,
                providerId = 20L
            )
        )
        val repository = WorkerRepositoryImpl(FakeWorkerService(allWorkersResponse = Response.success(dtos)))

        val result = repository.getAllWorkers()

        assertTrue(result.isSuccess)
        assertEquals(
            listOf(
                Worker(1L, "Ana Lopez", "Maquillista", "https://example.com/ana.jpg", 10L),
                Worker(2L, "Luis Perez", null, null, 20L)
            ),
            result.getOrNull()
        )
    }

    @Test
    fun getAllWorkersWhenResponseIsNotSuccessful() = runBlocking {
        val repository = WorkerRepositoryImpl(
            FakeWorkerService(
                allWorkersResponse = Response.error(
                    404,
                    "Not Found".toResponseBody(null)
                )
            )
        )

        val result = repository.getAllWorkers()

        assertFalse(result.isSuccess)
        assertEquals("HTTP 404", result.exceptionOrNull()?.message)
    }

    @Test
    fun getAllWorkersWhenServiceThrowsException() = runBlocking {
        val repository = WorkerRepositoryImpl(
            FakeWorkerService(
                allWorkersException = IOException("Network error")
            )
        )

        val result = repository.getAllWorkers()

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is IOException)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}

private class FakeWorkerService(
    private val allWorkersResponse: Response<List<WorkerDto>>? = null,
    private val allWorkersException: Exception? = null
) : WorkerService {

    override suspend fun getAllWorkers(): Response<List<WorkerDto>> {
        allWorkersException?.let { throw it }
        return allWorkersResponse
            ?: throw IllegalStateException("A response or exception must be provided for test setup.")
    }

    override suspend fun getWorkerById(workerId: Long): Response<WorkerDto> {
        throw UnsupportedOperationException("Not used in WorkerRepositoryImpl tests.")
    }
}

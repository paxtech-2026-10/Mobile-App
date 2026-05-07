package com.paxtech.mobileapp.features.clientDashboard.data.repository

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.WorkerService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WorkerRepositoryImplIntegrationTest {

    private lateinit var server: MockWebServer
    private lateinit var repository: WorkerRepositoryImpl

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WorkerService::class.java)
        repository = WorkerRepositoryImpl(service)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun getAllWorkersWithRealRetrofitAndJson() {
        runBlocking {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(
                        """
                        [
                          {
                            "id": 1,
                            "name": "Ana Lopez",
                            "specialization": "Electricista",
                            "photoUrl": "https://example.com/ana.jpg",
                            "providerId": 10
                          },
                          {
                            "id": 2,
                            "name": "Luis Perez",
                            "specialization": null,
                            "photoUrl": null,
                            "providerId": 20
                          }
                        ]
                        """.trimIndent()
                    )
            )

            val result = repository.getAllWorkers()

            assertTrue(result.isSuccess)
            val workers = result.getOrNull()
            assertNotNull(workers)
            assertEquals(2, workers.size)
            assertEquals("Electricista", workers[0].specialization)
            assertEquals("Luis Perez", workers[1].name)
        }
    }

    @Test
    fun getAllWorkersWhenApiReturnsEmptyArray() {
        runBlocking {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("[]")
            )

            val result = repository.getAllWorkers()

            assertTrue(result.isSuccess)
            assertEquals(emptyList(), result.getOrNull())
        }
    }

    @Test
    fun getAllWorkersWhenApiReturnsHttpError() {
        runBlocking {
            server.enqueue(
                MockResponse()
                    .setResponseCode(500)
                    .setBody("""{"message":"Internal error"}""")
            )

            val result = repository.getAllWorkers()

            assertFalse(result.isSuccess)
            assertEquals("HTTP 500", result.exceptionOrNull()?.message)
        }
    }

    @Test
    fun getAllWorkersWhenApiReturnsMalformedJson() {
        runBlocking {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("""{"invalid":"payload"}""")
            )

            val result = repository.getAllWorkers()

            assertFalse(result.isSuccess)
            assertNotNull(result.exceptionOrNull())
        }
    }
}

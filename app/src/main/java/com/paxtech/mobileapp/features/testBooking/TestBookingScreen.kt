package com.paxtech.mobileapp.features.testBooking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.TimeSlotService
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.TimeSlotDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Composable
fun TestBookingScreen(
    viewModel: TestBookingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Test JWT - Time Slots",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Endpoint: GET /api/v1/time-slots",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Button(
            onClick = { viewModel.testTimeSlots() },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isLoading) "Cargando..." else "Probar Endpoint")
        }

        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            state.error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "❌ Error:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            state.result != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "✅ Resultado:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.result ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        if (state.responseCode != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📊 Información de la Respuesta:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Status Code: ${state.responseCode}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Success: ${state.isSuccess}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (state.headers != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Headers:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = state.headers ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@HiltViewModel
class TestBookingViewModel @Inject constructor(
    private val timeSlotService: TimeSlotService
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestBookingUiState())
    val uiState: StateFlow<TestBookingUiState> = _uiState.asStateFlow()

    fun testTimeSlots() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, result = null)
            try {
                val response = withContext(Dispatchers.IO) {
                    timeSlotService.getAllTimeSlots()
                }

                val responseCode = response.code()
                val isSuccess = response.isSuccessful
                val headers = response.headers().toString()

                if (isSuccess) {
                    val body = response.body()
                    val resultText = if (body != null) {
                        "✅ Éxito!\n\n" +
                        "Total de time slots: ${body.size}\n\n" +
                        body.joinToString("\n\n") { slot ->
                            "ID: ${slot.id}\n" +
                            "Start: ${slot.startTime}\n" +
                            "End: ${slot.endTime}\n" +
                            "Status: ${slot.status}\n" +
                            "Type: ${slot.type}"
                        }
                    } else {
                        "✅ Éxito pero el body está vacío"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        result = resultText,
                        responseCode = responseCode,
                        isSuccess = isSuccess,
                        headers = headers
                    )
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Sin mensaje de error"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "❌ Error ${responseCode}\n\n$errorBody",
                        responseCode = responseCode,
                        isSuccess = false,
                        headers = headers
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "❌ Excepción: ${e.message}\n\n${e.stackTraceToString()}"
                )
            }
        }
    }
}

data class TestBookingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val result: String? = null,
    val responseCode: Int? = null,
    val isSuccess: Boolean? = null,
    val headers: String? = null
)


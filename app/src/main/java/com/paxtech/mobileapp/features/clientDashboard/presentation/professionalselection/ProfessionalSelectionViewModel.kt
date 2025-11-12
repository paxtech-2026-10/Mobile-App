package com.paxtech.mobileapp.features.clientDashboard.presentation.professionalselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.WorkerRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.models.Worker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfessionalUi(
    val id: Long,
    val name: String,
    val imageUrl: String?
)

@HiltViewModel
class ProfessionalSelectionViewModel @Inject constructor(
    private val workerRepository: WorkerRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _professionals = MutableStateFlow<List<ProfessionalUi>>(emptyList())
    val professionals: StateFlow<List<ProfessionalUi>> = _professionals

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadWorkers(providerId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            println("🔍 ProfessionalSelectionViewModel: Cargando workers para providerId: $providerId")
            val result = workerRepository.getAllWorkers()
            result.onSuccess { workers ->
                println("🔍 ProfessionalSelectionViewModel: Total de workers obtenidos: ${workers.size}")
                // Filtrar workers por providerId
                val filteredWorkers = workers.filter { it.providerId == providerId }
                println("🔍 ProfessionalSelectionViewModel: Workers filtrados para providerId $providerId: ${filteredWorkers.size}")
                _professionals.value = filteredWorkers.map { it.toUi() }
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Error desconocido"
                println("🔍 ProfessionalSelectionViewModel: Error al cargar workers: ${e.message}")
            }
            _isLoading.value = false
        }
    }
}

private fun Worker.toUi(): ProfessionalUi = ProfessionalUi(
    id = id,
    name = name,
    imageUrl = photoUrl
)




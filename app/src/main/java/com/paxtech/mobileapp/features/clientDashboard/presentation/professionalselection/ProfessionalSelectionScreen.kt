package com.paxtech.mobileapp.features.clientDashboard.presentation.professionalselection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.paxtech.mobileapp.features.clientDashboard.presentation.professionalselection.ProfessionalSelectionViewModel
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi
import com.paxtech.mobileapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalSelectionScreen(
    service: ServiceUi,
    onBack: () -> Unit,
    onContinue: (selectedProfessional: String, workerId: Long) -> Unit,
    viewModel: ProfessionalSelectionViewModel = hiltViewModel()
) {
    var selectedProfessional by remember { mutableStateOf("") }
    var selectedWorkerId by remember { mutableStateOf(0L) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val loadedProfessionals by viewModel.professionals.collectAsState()

    val professionals = remember(loadedProfessionals) {
        val mapped = loadedProfessionals.map { p ->
            // Detectar género basándose en el photoUrl: /men/ = hombre (azul), /women/ = mujer (rosa)
            val isMale = p.imageUrl?.contains("/men/") == true
            val background = if (isMale) Color(0xFFD6F0FF) else Color(0xFFFFE0E6) // Azul para hombres, rosa para mujeres
            Professional(name = p.name, imageUrl = p.imageUrl, isAnyProfessional = false, backgroundColor = background, workerId = p.id)
        }
        mapped + Professional("Cualquier profesional", null, true, Color(0xFFEDE7F6), workerId = 0L)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Seleccionar profesional",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF5F5FF)
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = { onContinue(selectedProfessional, selectedWorkerId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        "Continuar",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F5FF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage ?: "", color = Color.Red)
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mostrar profesionales en filas de 2
                items(professionals.chunked(2).size) { index ->
                    val rowProfessionals = professionals.chunked(2)[index]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (rowProfessionals.size == 1) {
                            Arrangement.Center
                        } else {
                            Arrangement.spacedBy(12.dp)
                        }
                    ) {
                        rowProfessionals.forEach { professional ->
                            if (rowProfessionals.size == 1) {
                                // Card centrado para "Cualquier profesional"
                                Box(
                                    modifier = Modifier
                                        .width(160.dp)
                                ) {
                                    ProfessionalCard(
                                        professional = professional,
                                        isSelected = selectedProfessional == professional.name,
                                        onSelect = { 
                                            selectedProfessional = professional.name
                                            selectedWorkerId = professional.workerId
                                        }
                                    )
                                }
                            } else {
                                // Cards normales ocupando la mitad del ancho
                                Box(modifier = Modifier.weight(1f)) {
                                    ProfessionalCard(
                                        professional = professional,
                                        isSelected = selectedProfessional == professional.name,
                                        onSelect = { 
                                            selectedProfessional = professional.name
                                            selectedWorkerId = professional.workerId
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Spacer al final para que se vea completo
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            }
        }
    }
}

data class Professional(
    val name: String,
    val imageUrl: String?,
    val isAnyProfessional: Boolean,
    val backgroundColor: Color,
    val workerId: Long = 0L
)

@Composable
private fun ProfessionalCard(
    professional: Professional,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .background(professional.backgroundColor)
            .clickable { onSelect() }
    ) {
        // Círculo de selección en esquina superior derecha
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(22.dp)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray.copy(alpha = 0.4f), CircleShape)
                )
            }
        }

        // Contenido centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Avatar o icono
            if (professional.isAnyProfessional) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFBDBDBD)),
                        contentAlignment = Alignment.Center
                    ) {}
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(30.dp)
                            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                            .background(Color(0xFFBDBDBD))
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = professional.imageUrl,
                        contentDescription = professional.name,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre
            Text(
                text = professional.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}


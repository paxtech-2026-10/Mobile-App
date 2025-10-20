package com.paxtech.mobileapp.features.clientDashboard.presentation.professionalselection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalSelectionScreen(
    service: ServiceUi,
    onBack: () -> Unit,
    onContinue: (selectedProfessional: String) -> Unit
) {
    var selectedProfessional by remember { mutableStateOf("Cualquier profesional") }
    val professionals = listOf(
        Professional("Cualquier profesional", null, true),
        Professional("Pedro", "url_image", false),
        Professional("Jose", "url_image", false),
        Professional("Ana", "url_image", false),
        Professional("Carla", "url_image", false)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Seleccionar profesional",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            service.price,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${service.title} - ${service.durationMins} min",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            selectedProfessional,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                    Button(
                        onClick = { onContinue(selectedProfessional) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8DEF8)
                        ),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "Continuar",
                            color = Color(0xFF6750A4),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(professionals) { professional ->
                ProfessionalItem(
                    professional = professional,
                    isSelected = selectedProfessional == professional.name,
                    onSelect = { selectedProfessional = professional.name }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            }
        }
    }
}

data class Professional(
    val name: String,
    val imageUrl: String?,
    val isAnyProfessional: Boolean
)

@Composable
private fun ProfessionalItem(
    professional: Professional,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (professional.isAnyProfessional) {
                // Icono de grupo de personas
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8E8E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Gray
                    )
                }
            } else {
                // Imagen del profesional
                AsyncImage(
                    model = professional.imageUrl,
                    contentDescription = professional.name,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            }

            Text(
                text = professional.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }

        if (isSelected) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = Color(0xFFE8DEF8)
            ) {
                Text(
                    text = "Seleccionado",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color(0xFF6750A4),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            OutlinedButton(
                onClick = onSelect,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Text("Seleccionar", fontSize = 13.sp)
            }
        }
    }
}
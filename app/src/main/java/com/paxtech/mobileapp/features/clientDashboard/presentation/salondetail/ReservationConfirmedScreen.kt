package com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.paxtech.mobileapp.features.clientDashboard.presentation.shared.ReservationData

@Composable
fun ReservationConfirmedScreen(
    reservationData: ReservationData,
    onBackToHome: () -> Unit
) {
    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            Text("¡Cita confirmada!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Text(
                "Tu reserva ha sido confirmada exitosamente. Te hemos enviado un correo con todos los detalles.",
                fontSize = 15.sp, color = Color(0xFF666666), textAlign = TextAlign.Center, lineHeight = 22.sp
            )
            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = reservationData.salonImageUrl,
                            contentDescription = reservationData.salonName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(reservationData.salonName, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                            Text(reservationData.salonAddress, fontSize = 13.sp, color = Color(0xFF666666))
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Detail("Servicio",      reservationData.service.title)
                    Detail("Profesional",   reservationData.selectedProfessional)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(Modifier.weight(1f)) { Detail("Fecha", reservationData.formattedDate) }
                        Column(Modifier.weight(1f)) { Detail("Hora",  reservationData.formattedTime) }
                    }
                    Detail("Duración", "${reservationData.service.durationMins} minutos")

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Total pagado", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                        Text(reservationData.service.price, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DEF8))
            ) { Text("Volver al inicio", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6750A4)) }
        }
    }
}

@Composable
private fun Detail(title: String, value: String) {
    Column {
        Text(title, fontSize = 13.sp, color = Color(0xFF666666))
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        Spacer(Modifier.height(12.dp))
    }
}

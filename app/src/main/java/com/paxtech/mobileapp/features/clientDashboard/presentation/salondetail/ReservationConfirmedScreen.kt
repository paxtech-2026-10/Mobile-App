package com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paxtech.mobileapp.features.clientDashboard.presentation.shared.ReservationData
import com.paxtech.mobileapp.ui.theme.BackgroundPurpleLight
import com.paxtech.mobileapp.ui.theme.PrimaryPurple

@Composable
fun ReservationConfirmedScreen(
    reservationData: ReservationData,
    onCancel: () -> Unit,          // ← NUEVO: callback para volver atrás
    onBackToHome: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundPurpleLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Título
            Text(
                "Congratulation",
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF2D3142),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Card blanca principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icono de check con forma de estrella/sol con puntas
                    Box(contentAlignment = Alignment.Center) {
                        // Forma de estrella/sol con puntas
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .drawBehind {
                                    val path = Path()
                                    val centerX = size.width / 2
                                    val centerY = size.height / 2
                                    val outerRadius = size.width / 2
                                    val innerRadius = size.width / 2.8f
                                    val points = 12 // 12 puntas

                                    for (i in 0 until points * 2) {
                                        val angle = (i * Math.PI / points).toFloat()
                                        val radius = if (i % 2 == 0) outerRadius else innerRadius
                                        val x = centerX + radius * cos(angle.toDouble()).toFloat()
                                        val y = centerY + radius * sin(angle.toDouble()).toFloat()

                                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                    }
                                    path.close()

                                    drawPath(
                                        path = path,
                                        color = Color(0xFFE9D5FF),
                                        style = Fill
                                    )
                                }
                        )

                        // Círculo central con el check
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(color = Color(0xFFE9D5FF), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Success",
                                tint = PrimaryPurple,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Título de éxito
                    Text(
                        "Order Successfully",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W700,
                        color = Color(0xFF2D3142),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    // Descripción
                    Text(
                        "Tu reserva ha sido confirmada exitosamente. Te hemos enviado un correo con todos los detalles.",
                        fontSize = 13.sp,
                        color = Color(0xFF7A7A7A),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    // Date & Time
                    InfoRowConfirmed(
                        label = "Date & Time:",
                        value = "${reservationData.formattedDate} - ${reservationData.formattedTime}"
                    )

                    Spacer(Modifier.height(16.dp))

                    // Estilista
                    InfoRowConfirmed(
                        label = "Estilista:",
                        value = reservationData.selectedProfessional
                    )

                    Spacer(Modifier.height(20.dp))

                    // Service List Header
                    Text(
                        "Service List",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFF2D3142),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    // Servicio principal
                    ServiceRowConfirmed(
                        name = reservationData.service.title,
                        duration = "${reservationData.service.durationMins} min",
                        price = reservationData.service.price
                    )

                    Spacer(Modifier.height(8.dp))

                    // CGST
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("CGST", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                        Text("$5", fontSize = 13.sp, color = Color(0xFF2D3142))
                    }
                    Spacer(Modifier.height(8.dp))

                    // SGST
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("SGST", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                        Text("$5", fontSize = 13.sp, color = Color(0xFF2D3142))
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE8E8E8))
                    Spacer(Modifier.height(16.dp))

                    // Total Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Time", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                        Text("${reservationData.service.durationMins} Minutes", fontSize = 13.sp, color = Color(0xFF2D3142))
                    }
                    Spacer(Modifier.height(8.dp))

                    // Calcular subtotal
                    val basePrice = extractPriceValue(reservationData.service.price)
                    val subtotal = basePrice + 10.0 // CGST + SGST

                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                        Text("$${String.format("%.2f", subtotal)}", fontSize = 13.sp, color = Color(0xFF2D3142))
                    }
                    Spacer(Modifier.height(8.dp))

                    // Coupon Discount
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Coupon Discount", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                        Text("-$0.00", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE8E8E8))
                    Spacer(Modifier.height(12.dp))

                    // Total Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total Price",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF2D3142)
                        )
                        Text(
                            "$${String.format("%.2f", subtotal)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W700,
                            color = Color(0xFF2D3142)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón Cancelar -> vuelve a la pantalla anterior
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PrimaryPurple
                            )
                        ) {
                            Text(
                                "Cancelar",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600
                            )
                        }

                        // Botón Back to Home
                        Button(
                            onClick = onBackToHome,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryPurple
                            )
                        ) {
                            Text(
                                "Back to Home",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoRowConfirmed(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF7A7A7A)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.W500,
            color = Color(0xFF2D3142)
        )
    }
}

@Composable
private fun ServiceRowConfirmed(name: String, duration: String, price: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 13.sp,
            color = Color(0xFF7A7A7A),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = duration,
            fontSize = 13.sp,
            color = Color(0xFF7A7A7A),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Text(
            text = price,
            fontSize = 13.sp,
            fontWeight = FontWeight.W500,
            color = Color(0xFF2D3142)
        )
    }
}

private fun extractPriceValue(priceString: String): Double {
    return try {
        priceString
            .replace("S/", "", ignoreCase = true)
            .replace("$", "")
            .replace(",", "")
            .trim()
            .toDoubleOrNull() ?: 0.0
    } catch (_: Exception) {
        0.0
    }
}

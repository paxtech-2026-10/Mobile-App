package com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.paxtech.mobileapp.ui.theme.PrimaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    reservationDetails: ReservationDetails,
    clientId: Long,
    providerId: Long,
    serviceId: Long,
    timeSlotId: Long,
    workerId: Long,
    onBack: () -> Unit,
    onPaymentLinkReady: (reservationId: Long, paymentId: Long, paymentLinkUrl: String, discountTitle: String?, discountAmount: Double, discountType: String?) -> Unit,
    onError: (String) -> Unit
) {
    val viewModel: ConfirmationViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appliedDiscount by viewModel.appliedDiscount.collectAsStateWithLifecycle()
    val couponError by viewModel.couponError.collectAsStateWithLifecycle()
    
    // Estado local para el input de cupón
    var couponCode by remember { mutableStateOf("") }
    
    val basePrice = extractPrice(reservationDetails.totalPrice)
    
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ConfirmationUiState.PaymentLinkReady -> {
                // Pasar información del descuento aplicado
                val discountTitle = appliedDiscount?.title
                val discountAmountValue = viewModel.calculateDiscountAmount(basePrice)
                val discountTypeValue = appliedDiscount?.discountType?.name
                onPaymentLinkReady(
                    state.reservationId, 
                    state.paymentId, 
                    state.paymentLinkUrl,
                    discountTitle,
                    discountAmountValue,
                    discountTypeValue
                )
            }
            is ConfirmationUiState.Error -> {
                onError(state.message)
            }
            else -> {}
        }
    }

    val subtotal = basePrice
    
    // Calcular descuento y precio final
    val discountAmount = viewModel.calculateDiscountAmount(basePrice)
    val finalTotal = viewModel.calculateFinalPrice(basePrice)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Your Appointment",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFF2D3142)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2D3142)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF2F1FF)
                )
            )
        },
        containerColor = Color(0xFFF2F1FF)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Banner morado con esquinas redondeadas superiores
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(PrimaryPurple)
                        .padding(16.dp)
                        .padding(bottom = 24.dp)
                ) {
                    // CARD BLANCA - Info del Salón
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFEDE9FE))
                            ) {
                                AsyncImage(
                                    model = reservationDetails.salonImageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reservationDetails.salonName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.W600,
                                    color = Color(0xFF2D3142)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = reservationDetails.address.take(25) + "...",
                                        fontSize = 11.sp,
                                        color = Color(0xFF7A7A7A),
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                // TODO: Calcular distancia real usando la ubicación del usuario y del salón
                                // Text(
                                //     text = "↔ 5 km",  // Comentado: Distancia hardcodeada
                                //     fontSize = 11.sp,
                                //     color = Color(0xFF7A7A7A)
                                // )
                            }
                        }
                    }
                }

                // CARD BLANCA - Se superpone con offset negativo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-24).dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                            .padding(bottom = 90.dp)
                    ) {
                        // Date & Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Date & Time:",
                                fontSize = 13.sp,
                                color = Color(0xFF7A7A7A)
                            )
                            Text(
                                text = "${reservationDetails.date} - ${reservationDetails.time}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.W500,
                                color = Color(0xFF2D3142)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gender Type
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Gender Type:",
                                fontSize = 13.sp,
                                color = Color(0xFF7A7A7A)
                            )
                            Text(
                                text = reservationDetails.professional,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.W500,
                                color = Color(0xFF2D3142)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Service List",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF2D3142)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ServiceRow(
                            name = reservationDetails.serviceName,
                            duration = "${reservationDetails.duration} min",
                            price = reservationDetails.totalPrice
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // TODO: Mostrar impuestos si vienen del backend o configuración del salón
                        // Row(
                        //     modifier = Modifier.fillMaxWidth(),
                        //     horizontalArrangement = Arrangement.SpaceBetween
                        // ) {
                        //     Text("CGST", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                        //     Text("$5", fontSize = 13.sp, color = Color(0xFF2D3142))  // Comentado: Impuesto hardcodeado
                        // }
                        //
                        // Spacer(modifier = Modifier.height(8.dp))
                        //
                        // Row(
                        //     modifier = Modifier.fillMaxWidth(),
                        //     horizontalArrangement = Arrangement.SpaceBetween
                        // ) {
                        //     Text("SGST", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                        //     Text("$5", fontSize = 13.sp, color = Color(0xFF2D3142))  // Comentado: Impuesto hardcodeado
                        // }

                        Spacer(modifier = Modifier.height(20.dp))

                        // ========== SECCIÓN DE CUPÓN ==========
                        Text(
                            text = "Apply Coupon",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF2D3142)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = couponCode,
                                onValueChange = { 
                                    couponCode = it
                                    // Limpiar error cuando el usuario escribe
                                    if (couponError != null) {
                                        viewModel.removeCoupon()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                placeholder = {
                                    Text(
                                        "Ingresa el título del descuento",
                                        fontSize = 13.sp,
                                        color = Color(0xFFAAAAAA)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalOffer,
                                        contentDescription = null,
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (appliedDiscount != null) {
                                        IconButton(onClick = {
                                            couponCode = ""
                                            viewModel.removeCoupon()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remover cupón",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                },
                                isError = couponError != null,
                                supportingText = {
                                    if (couponError != null) {
                                        Text(
                                            text = couponError!!,
                                            color = Color(0xFFEF4444),
                                            fontSize = 12.sp
                                        )
                                    } else if (appliedDiscount != null) {
                                        Text(
                                            text = "Cupón aplicado: ${appliedDiscount!!.title}",
                                            color = Color(0xFF10B981),
                                            fontSize = 12.sp
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = if (appliedDiscount != null) Color(0xFF10B981) else Color(0xFFE8E8E8),
                                    focusedBorderColor = PrimaryPurple,
                                    unfocusedContainerColor = Color(0xFFFAFAFA),
                                    focusedContainerColor = Color(0xFFFAFAFA),
                                    errorBorderColor = Color(0xFFEF4444)
                                ),
                                singleLine = true,
                                enabled = appliedDiscount == null  // Deshabilitar cuando hay cupón aplicado
                            )

                            Button(
                                onClick = {
                                    viewModel.applyCoupon(couponCode.trim(), providerId)
                                },
                                modifier = Modifier.height(56.dp),
                                enabled = couponCode.isNotBlank() && appliedDiscount == null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryPurple,
                                    disabledContainerColor = Color(0xFFE8E8E8)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp)
                            ) {
                                Text(
                                    if (appliedDiscount != null) "Aplicado" else "Aplicar",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W600,
                                    color = if (appliedDiscount != null) Color(0xFF10B981) else Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        // ========== FIN SECCIÓN DE CUPÓN ==========

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Time", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                            Text(
                                "${reservationDetails.duration} Minutes",
                                fontSize = 13.sp,
                                color = Color(0xFF2D3142)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                            Text(
                                "$${String.format("%.2f", subtotal)}",
                                fontSize = 13.sp,
                                color = Color(0xFF2D3142)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Mostrar descuento si está aplicado
                        if (appliedDiscount != null && discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Descuento (${appliedDiscount!!.title})",
                                    fontSize = 13.sp,
                                    color = Color(0xFF7A7A7A)
                                )
                                Text(
                                    "-$${String.format("%.2f", discountAmount)}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.W500
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE8E8E8))
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Price",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                color = Color(0xFF2D3142)
                            )
                            Text(
                                text = "$${String.format("%.2f", finalTotal)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W700,
                                color = Color(0xFF2D3142)
                            )
                        }
                    }
                }
            }

            // BOTTOM BAR
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total",
                            fontSize = 13.sp,
                            color = Color(0xFF7A7A7A),
                            fontWeight = FontWeight.W400
                        )
                        Text(
                            text = "$${String.format("%.2f", finalTotal)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W700,
                            color = Color(0xFF2D3142)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.createReservationAndStartPayment(
                                clientId = clientId,
                                providerId = providerId,
                                serviceId = serviceId,
                                timeSlotId = timeSlotId,
                                workerId = workerId,
                                baseAmount = basePrice  // Pasar precio base, el ViewModel calculará el descuento
                            )
                        },
                        modifier = Modifier
                            .width(180.dp)
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple
                        ),
                        shape = RoundedCornerShape(27.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text = "Confirmar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceRow(name: String, duration: String, price: String) {
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

private fun extractPrice(priceString: String): Double {
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

data class ReservationDetails(
    val salonName: String,
    val rating: Double,
    val address: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val duration: Int,
    val professional: String,
    val totalPrice: String,
    val salonImageUrl: String = ""  // Eliminada URL hardcodeada, siempre se pasa desde AppNav
)


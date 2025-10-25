package com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    var couponCode by remember { mutableStateOf("") }
    var appliedDiscount by remember { mutableStateOf(0.0) }

    val basePrice = extractPrice(reservationDetails.totalPrice)
    val cgst = 5.0
    val sgst = 5.0
    val subtotal = basePrice + cgst + sgst
    val finalTotal = subtotal - appliedDiscount

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

                                Text(
                                    text = "↔ 5 km",
                                    fontSize = 11.sp,
                                    color = Color(0xFF7A7A7A)
                                )
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("CGST", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                            Text("$5", fontSize = 13.sp, color = Color(0xFF2D3142))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("SGST", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                            Text("$5", fontSize = 13.sp, color = Color(0xFF2D3142))
                        }

                        Spacer(modifier = Modifier.height(20.dp))

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
                                onValueChange = { couponCode = it },
                                modifier = Modifier.weight(1f),
                                placeholder = {
                                    Text(
                                        "Enter coupon",
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
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFE8E8E8),
                                    focusedBorderColor = PrimaryPurple,
                                    unfocusedContainerColor = Color(0xFFFAFAFA),
                                    focusedContainerColor = Color(0xFFFAFAFA)
                                ),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    if (couponCode.isNotEmpty()) {
                                        appliedDiscount = 15.0
                                    }
                                },
                                modifier = Modifier.height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryPurple
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp)
                            ) {
                                Text(
                                    "Apply",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W600
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Coupon Discount", fontSize = 13.sp, color = Color(0xFF7A7A7A))
                            Text(
                                "-$${String.format("%.2f", appliedDiscount)}",
                                fontSize = 13.sp,
                                color = if (appliedDiscount > 0) Color(0xFFEF4444) else Color(0xFF2D3142)
                            )
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
                        onClick = onConfirm,
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
    val salonImageUrl: String = "https://images.unsplash.com/photo-1562322140-8baeececf3df?q=80&w=1000"
)
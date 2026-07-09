package com.paxtech.mobileapp.features.payment.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import com.paxtech.mobileapp.ui.theme.PrimaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentProcessingScreen(
    paymentId: Long?,
    paymentLinkUrl: String?,
    onPaymentSucceeded: () -> Unit,
    onPaymentFailed: () -> Unit,
    onBack: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val paymentState by viewModel.paymentState.collectAsStateWithLifecycle()
    var hasOpenedBrowser by remember { mutableStateOf(false) }
    
    // Detectar cuando la app vuelve al foreground para verificar inmediatamente el estado del pago
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && paymentId != null && hasOpenedBrowser) {
                // Cuando la app vuelve al foreground, verificar inmediatamente el estado del pago
                println("🔍 PaymentProcessingScreen: App resumed, checking payment status immediately")
                viewModel.checkPaymentStatusImmediately(paymentId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    LaunchedEffect(paymentId, paymentLinkUrl) {
        if (paymentId != null && paymentLinkUrl != null && !hasOpenedBrowser) {
            // Abrir el navegador con el link de Stripe
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentLinkUrl))
            context.startActivity(intent)
            hasOpenedBrowser = true

            // Iniciar polling después de abrir el navegador
            viewModel.startPollingPaymentStatus(paymentId)

            // Simulación: a los 10s se aprueba el pago automáticamente (si el backend
            // tiene PAYMENTS_SIMULATION_ENABLED=true), sin esperar el webhook de Stripe.
            viewModel.startPaymentSimulation(paymentId)
        }
    }
    
    LaunchedEffect(paymentState) {
        when (paymentState) {
            is PaymentState.PaymentSucceeded -> {
                onPaymentSucceeded()
            }
            is PaymentState.PaymentFailed, is PaymentState.PaymentTimeout, is PaymentState.Error -> {
                onPaymentFailed()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Procesando Pago",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFF2D3142)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
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
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (val state = paymentState) {
                        is PaymentState.Idle -> {
                            CircularProgressIndicator(color = PrimaryPurple)
                            Text(
                                "Iniciando pago...",
                                fontSize = 16.sp,
                                color = Color(0xFF2D3142)
                            )
                        }
                        is PaymentState.CreatingPayment -> {
                            CircularProgressIndicator(color = PrimaryPurple)
                            Text(
                                "Creando pago...",
                                fontSize = 16.sp,
                                color = Color(0xFF2D3142)
                            )
                        }
                        is PaymentState.CreatingPaymentLink -> {
                            CircularProgressIndicator(color = PrimaryPurple)
                            Text(
                                "Generando link de pago...",
                                fontSize = 16.sp,
                                color = Color(0xFF2D3142)
                            )
                        }
                        is PaymentState.PaymentLinkReady -> {
                            CircularProgressIndicator(color = PrimaryPurple)
                            Text(
                                "Redirigiendo a Stripe...",
                                fontSize = 16.sp,
                                color = Color(0xFF2D3142)
                            )
                        }
                        is PaymentState.PollingPayment -> {
                            CircularProgressIndicator(color = PrimaryPurple)
                            Text(
                                "Esperando confirmación del pago...",
                                fontSize = 16.sp,
                                color = Color(0xFF2D3142)
                            )
                            Text(
                                "Por favor, completa el pago en el navegador",
                                fontSize = 14.sp,
                                color = Color(0xFF7A7A7A)
                            )
                            LinearProgressIndicator(
                                progress = { state.currentAttempt.toFloat() / state.maxAttempts.toFloat() },
                                modifier = Modifier.fillMaxWidth(),
                                color = PrimaryPurple
                            )
                        }
                        is PaymentState.PaymentSucceeded -> {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                "¡Pago exitoso!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W600,
                                color = Color(0xFF2D3142)
                            )
                        }
                        is PaymentState.PaymentFailed -> {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                "Error en el pago",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W600,
                                color = Color(0xFF2D3142)
                            )
                            Text(
                                state.message,
                                fontSize = 14.sp,
                                color = Color(0xFF7A7A7A)
                            )
                        }
                        is PaymentState.PaymentTimeout -> {
                            Icon(
                                imageVector = Icons.Filled.AccessTime,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                "Tiempo agotado",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W600,
                                color = Color(0xFF2D3142)
                            )
                            Text(
                                state.message,
                                fontSize = 14.sp,
                                color = Color(0xFF7A7A7A)
                            )
                        }
                        is PaymentState.Error -> {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                "Error",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W600,
                                color = Color(0xFF2D3142)
                            )
                            Text(
                                state.message,
                                fontSize = 14.sp,
                                color = Color(0xFF7A7A7A)
                            )
                        }
                    }
                }
            }
        }
    }
}


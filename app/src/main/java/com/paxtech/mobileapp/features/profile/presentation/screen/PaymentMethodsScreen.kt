package com.paxtech.mobileapp.features.profile.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentCardBrand
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodUi
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodsUiState
import com.paxtech.mobileapp.ui.theme.BackgroundGray
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.DividerGray
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    state: PaymentMethodsUiState,
    onBack: () -> Unit,
    onAddPaymentMethod: () -> Unit,
    onEditPaymentMethod: (PaymentMethodUi) -> Unit,
    onDeletePaymentMethod: (PaymentMethodUi) -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingDeletion = state.methodPendingDeletion

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Métodos de pago",
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundWhite,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Agregar tarjeta") },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                onClick = onAddPaymentMethod,
                containerColor = PrimaryPurple,
                contentColor = BackgroundWhite
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            }

            state.methods.isEmpty() -> {
                EmptyPaymentMethods(modifier = Modifier.padding(innerPadding))
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.methods, key = { it.id }) { method ->
                        PaymentMethodItem(
                            method = method,
                            onEdit = { onEditPaymentMethod(method) },
                            onDelete = { onDeletePaymentMethod(method) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (pendingDeletion != null) {
        AlertDialog(
            onDismissRequest = onDismissDelete,
            confirmButton = {
                TextButton(onClick = onConfirmDelete) {
                    Text(text = "Sí, eliminar", color = PrimaryPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDelete) {
                    Text(text = "Cancelar", color = TextSecondary)
                }
            },
            title = {
                Text(
                    text = "Eliminar método",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = "¿Seguro que deseas eliminar la tarjeta terminada en ${pendingDeletion.lastFourDigits}?",
                    color = TextSecondary
                )
            }
        )
    }
}

@Composable
private fun EmptyPaymentMethods(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aún no has agregado un método de pago",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Agrega una tarjeta para agilizar tus futuras reservas.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun PaymentMethodItem(
    method: PaymentMethodUi,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = BorderStroke(
            width = 1.dp,
            color = DividerGray
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BrandBadge(brand = method.brand)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = method.brand.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = method.maskedNumber,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Editar") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = TextPrimary
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Eliminar") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PaymentInfoChip(label = "Titular", value = method.cardHolder)
                PaymentInfoChip(label = "Vence", value = method.expiryLabel)
            }
        }
    }
}

@Composable
private fun PaymentInfoChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundGray)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
private fun BrandBadge(brand: PaymentCardBrand, modifier: Modifier = Modifier) {
    val (startColor, endColor) = brandGradient(brand)
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(brush = Brush.linearGradient(listOf(startColor, endColor))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = brand.displayName.take(1),
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun brandGradient(brand: PaymentCardBrand): Pair<Color, Color> = when (brand) {
    PaymentCardBrand.VISA -> PrimaryPurple to PrimaryPurple.copy(alpha = 0.7f)
    PaymentCardBrand.MASTERCARD -> Color(0xFFE65C4F) to Color(0xFFF3A953)
    PaymentCardBrand.AMERICAN_EXPRESS -> Color(0xFF0E76BC) to Color(0xFF5FC9F8)
    PaymentCardBrand.DISCOVER -> Color(0xFF4A4A4A) to Color(0xFFFFB74D)
    PaymentCardBrand.UNKNOWN -> PrimaryPurple to PrimaryPurple.copy(alpha = 0.7f)
}

@Preview
@Composable
private fun PaymentMethodsScreenPreview() {
    val sampleMethods = listOf(
        PaymentMethodUi(
            id = "1",
            cardHolder = "Ibne Riead",
            cardNumber = "4111111111111111",
            expiryMonth = "05",
            expiryYear = "31",
            cvv = "123",
            brand = PaymentCardBrand.VISA
        ),
        PaymentMethodUi(
            id = "2",
            cardHolder = "Ibne Riead",
            cardNumber = "5555444433331111",
            expiryMonth = "08",
            expiryYear = "30",
            cvv = "123",
            brand = PaymentCardBrand.MASTERCARD
        )
    )
    MaterialTheme {
        PaymentMethodsScreen(
            state = PaymentMethodsUiState(isLoading = false, methods = sampleMethods),
            onBack = {},
            onAddPaymentMethod = {},
            onEditPaymentMethod = {},
            onDeletePaymentMethod = {},
            onDismissDelete = {},
            onConfirmDelete = {}
        )
    }
}

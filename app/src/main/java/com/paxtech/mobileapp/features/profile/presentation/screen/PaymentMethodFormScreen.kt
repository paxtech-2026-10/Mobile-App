package com.paxtech.mobileapp.features.profile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentCardBrand
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodFormField
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodFormState
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodsUiState
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodFormScreen(
    title: String,
    actionText: String,
    state: PaymentMethodsUiState,
    onBack: () -> Unit,
    onFieldChange: (PaymentMethodFormField, String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val form = state.form

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // Tarjeta visual de la tarjeta bancaria
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                val gradient = brandGradient(form.brand)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(gradient))
                        .padding(24.dp)
                ) {
                    Text(
                        text = form.brand.displayName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = BackgroundWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = form.maskedNumber,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = BackgroundWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column {
                            Text(
                                text = "Titular",
                                color = BackgroundWhite.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = form.cardHolder.ifBlank { "Nombre" },
                                color = BackgroundWhite,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column {
                            Text(
                                text = "Vence",
                                color = BackgroundWhite.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = form.expiryMonth.padStart(2, '0') +
                                        "/" + form.expiryYear.padStart(2, '0'),
                                color = BackgroundWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = form.cardHolder,
                onValueChange = { onFieldChange(PaymentMethodFormField.CardHolder, it) },
                label = { Text("Nombre del titular", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = form.cardNumber,
                onValueChange = { onFieldChange(PaymentMethodFormField.CardNumber, it) },
                label = { Text("Número de tarjeta", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                visualTransformation = CardNumberVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = form.expiryMonth,
                    onValueChange = {
                        onFieldChange(PaymentMethodFormField.ExpiryMonth, it.filter { c -> c.isDigit() }.take(2))
                    },
                    label = { Text("Mes", color = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = form.expiryYear,
                    onValueChange = {
                        onFieldChange(PaymentMethodFormField.ExpiryYear, it.filter { c -> c.isDigit() }.take(2))
                    },
                    label = { Text("Año", color = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = form.cvv,
                onValueChange = {
                    onFieldChange(
                        PaymentMethodFormField.Cvv,
                        it.filter { c -> c.isDigit() }.take(4)
                    )
                },
                label = { Text("CVV", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.NumberPassword
                ),
                visualTransformation = PasswordVisualTransformation()
            )

            if (state.formError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.formError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onSubmit,
                enabled = form.isValid,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    contentColor = BackgroundWhite
                )
            ) {
                Text(text = actionText)
            }
        }
    }
}

@Composable
private fun brandGradient(brand: PaymentCardBrand): List<Color> = when (brand) {
    PaymentCardBrand.VISA -> listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
    PaymentCardBrand.MASTERCARD -> listOf(Color(0xFFE52D27), Color(0xFFB31217))
    PaymentCardBrand.AMERICAN_EXPRESS -> listOf(Color(0xFF0B486B), Color(0xFFF56217))
    PaymentCardBrand.DISCOVER -> listOf(Color(0xFF614385), Color(0xFF516395))
    PaymentCardBrand.UNKNOWN -> listOf(
        PrimaryPurple,
        PrimaryPurple.copy(alpha = 0.6f)
    )
}

private class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.filter(Char::isDigit).take(19)
        val spaced = trimmed.chunked(4).joinToString(" ")

        val offsetMap = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var transformed = offset
                transformed += (offset / 4)
                return transformed.coerceAtMost(spaced.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val digits = spaced
                    .take(offset)
                    .count { it.isDigit() }
                return digits.coerceAtMost(trimmed.length)
            }
        }

        return TransformedText(
            text = AnnotatedString(spaced),
            offsetMapping = offsetMap
        )
    }
}

@Preview
@Composable
private fun PaymentMethodFormPreview() {
    val formState = PaymentMethodFormState(
        cardHolder = "Ibne Riead",
        cardNumber = "4111111111111111",
        expiryMonth = "05",
        expiryYear = "31",
        cvv = "123",
        brand = PaymentCardBrand.VISA,
        isValid = true
    )
    val uiState = PaymentMethodsUiState(
        isLoading = false,
        methods = emptyList(),
        form = formState
    )
    MaterialTheme {
        PaymentMethodFormScreen(
            title = "Agregar tarjeta",
            actionText = "Guardar",
            state = uiState,
            onBack = {},
            onFieldChange = { _, _ -> },
            onSubmit = {}
        )
    }
}

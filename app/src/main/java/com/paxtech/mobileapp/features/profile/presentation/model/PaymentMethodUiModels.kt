package com.paxtech.mobileapp.features.profile.presentation.model

import java.util.UUID

data class PaymentMethodUi(
    val id: String,
    val cardHolder: String,
    val cardNumber: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cvv: String,
    val brand: PaymentCardBrand
) {
    val lastFourDigits: String = cardNumber.takeLast(4).padStart(4, '•')

    val maskedNumber: String = buildString {
        val digits = cardNumber.filter(Char::isDigit)
        if (digits.isEmpty()) {
            append("•••• •••• •••• ••••")
        } else {
            val maskedDigits = digits.dropLast(4).map { '•' } + digits.takeLast(4).toList()
            maskedDigits.joinToString(separator = "")
                .chunked(4)
                .forEachIndexed { index, chunk ->
                    if (index != 0) append(' ')
                    append(chunk)
                }
        }
    }

    val expiryLabel: String = listOf(expiryMonth.padStart(2, '0'), expiryYear.padStart(2, '0')).joinToString("/")
}

enum class PaymentCardBrand(val displayName: String) {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMERICAN_EXPRESS("American Express"),
    DISCOVER("Discover"),
    UNKNOWN("Tarjeta");

    companion object {
        fun fromCardNumber(cardNumber: String): PaymentCardBrand {
            val digits = cardNumber.filter(Char::isDigit)
            return when {
                digits.startsWith("4") -> VISA
                digits.length >= 2 && digits.substring(0, 2).toIntOrNull() in 51..55 -> MASTERCARD
                digits.startsWith("34") || digits.startsWith("37") -> AMERICAN_EXPRESS
                digits.startsWith("6011") || digits.startsWith("65") -> DISCOVER
                else -> UNKNOWN
            }
        }
    }
}

enum class PaymentMethodFormField {
    CardHolder,
    CardNumber,
    ExpiryMonth,
    ExpiryYear,
    Cvv
}

data class PaymentMethodFormState(
    val id: String = "",
    val cardHolder: String = "",
    val cardNumber: String = "",
    val expiryMonth: String = "",
    val expiryYear: String = "",
    val cvv: String = "",
    val brand: PaymentCardBrand = PaymentCardBrand.UNKNOWN,
    val isValid: Boolean = false
) {
    fun update(field: PaymentMethodFormField, value: String): PaymentMethodFormState {
        val updated = when (field) {
            PaymentMethodFormField.CardHolder -> copy(cardHolder = value)
            PaymentMethodFormField.CardNumber -> copy(cardNumber = value.filter(Char::isDigit).take(19))
            PaymentMethodFormField.ExpiryMonth -> copy(expiryMonth = value.filter(Char::isDigit).take(2))
            PaymentMethodFormField.ExpiryYear -> copy(expiryYear = value.filter(Char::isDigit).take(2))
            PaymentMethodFormField.Cvv -> copy(cvv = value.filter(Char::isDigit).take(4))
        }
        return updated.withDerivedValues()
    }

    fun toPaymentMethodUi(generatedId: String = id.ifBlank { UUID.randomUUID().toString() }): PaymentMethodUi =
        PaymentMethodUi(
            id = generatedId,
            cardHolder = cardHolder.trim(),
            cardNumber = cardNumber.filter(Char::isDigit),
            expiryMonth = expiryMonth.padStart(2, '0'),
            expiryYear = expiryYear.padStart(2, '0'),
            cvv = cvv.filter(Char::isDigit),
            brand = brand
        )

    val maskedNumber: String
        get() {
            val digits = cardNumber.filter(Char::isDigit)
            if (digits.isEmpty()) return "•••• •••• •••• ••••"
            val chunks = digits.chunked(4)
            return chunks.joinToString(" ") { chunk ->
                if (chunk.length == 4) chunk else chunk.padEnd(4, '•')
            }
        }

    private fun withDerivedValues(): PaymentMethodFormState {
        val normalizedNumber = cardNumber.filter(Char::isDigit)
        val computedBrand = PaymentCardBrand.fromCardNumber(normalizedNumber)
        return copy(
            cardNumber = normalizedNumber,
            brand = computedBrand,
            isValid = isFormValid(normalizedNumber, computedBrand)
        )
    }

    private fun isFormValid(number: String, brand: PaymentCardBrand): Boolean {
        val month = expiryMonth.toIntOrNull() ?: -1
        val isMonthValid = month in 1..12
        val isYearValid = expiryYear.length == 2
        val isNumberValid = number.length in 13..19
        val isCvvValid = when (brand) {
            PaymentCardBrand.AMERICAN_EXPRESS -> cvv.length == 4
            else -> cvv.length in 3..4
        }
        return cardHolder.isNotBlank() && isNumberValid && isMonthValid && isYearValid && isCvvValid
    }

    companion object {
        fun fromPaymentMethod(method: PaymentMethodUi): PaymentMethodFormState = PaymentMethodFormState(
            id = method.id,
            cardHolder = method.cardHolder,
            cardNumber = method.cardNumber,
            expiryMonth = method.expiryMonth,
            expiryYear = method.expiryYear,
            cvv = method.cvv,
            brand = method.brand
        ).withDerivedValues()
    }
}

data class PaymentMethodsUiState(
    val isLoading: Boolean = true,
    val methods: List<PaymentMethodUi> = emptyList(),
    val form: PaymentMethodFormState = PaymentMethodFormState(),
    val formError: String? = null,
    val editingMethodId: String? = null,
    val methodPendingDeletion: PaymentMethodUi? = null,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false
)
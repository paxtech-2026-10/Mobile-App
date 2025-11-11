package com.paxtech.mobileapp.features.profile.presentation.model

enum class ChangePasswordField {
    CurrentPassword,
    NewPassword,
    ConfirmPassword
}

data class ChangePasswordFormState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val isValid: Boolean = false
) {
    fun update(field: ChangePasswordField, value: String): ChangePasswordFormState = when (field) {
        ChangePasswordField.CurrentPassword -> copy(currentPassword = value, currentPasswordError = null)
        ChangePasswordField.NewPassword -> copy(newPassword = value, newPasswordError = null)
        ChangePasswordField.ConfirmPassword -> copy(confirmPassword = value, confirmPasswordError = null)
    }.copy(isValid = false)

    fun validate(existingPassword: String?): ChangePasswordFormState {
        val trimmedCurrent = currentPassword.trim()
        val trimmedNew = newPassword.trim()
        val trimmedConfirm = confirmPassword.trim()

        val hasStoredPassword = !existingPassword.isNullOrEmpty()
        val currentError = when {
            trimmedCurrent.isEmpty() -> "Ingresa tu contraseña actual"
            hasStoredPassword && trimmedCurrent != existingPassword -> "La contraseña actual no es correcta"
            else -> null
        }

        val newError = when {
            trimmedNew.isEmpty() -> "Ingresa una nueva contraseña"
            trimmedNew.length < MIN_LENGTH -> "Usa al menos $MIN_LENGTH caracteres"
            trimmedNew == trimmedCurrent -> "La nueva contraseña no puede ser igual a la actual"
            else -> null
        }

        val confirmError = when {
            trimmedConfirm.isEmpty() -> "Confirma tu nueva contraseña"
            trimmedConfirm != trimmedNew -> "Las contraseñas no coinciden"
            else -> null
        }

        val isValid = currentError == null && newError == null && confirmError == null

        return copy(
            currentPassword = trimmedCurrent,
            newPassword = trimmedNew,
            confirmPassword = trimmedConfirm,
            currentPasswordError = currentError,
            newPasswordError = newError,
            confirmPasswordError = confirmError,
            isValid = isValid
        )
    }

    companion object {
        private const val MIN_LENGTH = 6

        fun empty(): ChangePasswordFormState = ChangePasswordFormState()
    }
}

data class ChangePasswordUiState(
    val form: ChangePasswordFormState = ChangePasswordFormState(),
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
package com.paxtech.mobileapp.features.profile.presentation.model

import android.util.Patterns

data class ProfileUi(
    val id: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val countryCode: String,
    val gender: GenderUi,
    val avatarUrl: String? = null
) {
    val firstName: String get() = fullName.substringBefore(" ", fullName)
}

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: ProfileUi? = null,
    val editForm: ProfileFormState = ProfileFormState(),
    val errorMessage: String? = null,
    val isProfileUpdated: Boolean = false,
    val isSaving: Boolean = false,
    val isLoggedOut: Boolean = false
) {
    companion object
}

data class ProfileFormState(
    val profileId: String = "",
    val fullName: String = "",
    val email: String = "",
    val countryCode: String = "+51",
    val phoneNumber: String = "",
    val gender: GenderUi = GenderUi.default,
    val isValid: Boolean = false
) {
    fun update(field: ProfileFormField, value: String): ProfileFormState = when (field) {
        ProfileFormField.FullName -> copy(fullName = value)
        ProfileFormField.Email -> copy(email = value)
        ProfileFormField.CountryCode -> copy(countryCode = value)
        ProfileFormField.PhoneNumber -> copy(phoneNumber = value)
    }.withValidation()

    fun updateGender(gender: GenderUi): ProfileFormState = copy(gender = gender).withValidation()

    fun toProfileUi(profileId: String, avatarUrl: String?): ProfileUi = ProfileUi(
        id = profileId,
        fullName = fullName.trim(),
        email = email.trim(),
        phoneNumber = phoneNumber.trim(),
        countryCode = countryCode.trim(),
        gender = gender,
        avatarUrl = avatarUrl
    )

    private fun withValidation(): ProfileFormState = copy(isValid = isFormValid())

    private fun isFormValid(): Boolean =
        fullName.isNotBlank() &&
                email.isValidEmail() &&
                countryCode.isNotBlank() &&
                phoneNumber.isNotBlank()

    companion object {
        fun fromProfile(profile: ProfileUi): ProfileFormState = ProfileFormState(
            profileId = profile.id,
            fullName = profile.fullName,
            email = profile.email,
            countryCode = profile.countryCode,
            phoneNumber = profile.phoneNumber,
            gender = profile.gender
        ).withValidation()
    }
}

enum class ProfileFormField {
    FullName,
    Email,
    CountryCode,
    PhoneNumber
}

enum class GenderUi(val label: String) {
    FEMALE("Femenino"),
    MALE("Masculino"),
    OTHER("Otro");

    companion object {
        val default: GenderUi = FEMALE
    }
}

fun ProfileUiState.Companion.sample(): ProfileUiState = ProfileUiState(
    isLoading = false,
    profile = ProfileUi(
        id = "profile-1",
        fullName = "Ione Riead",
        email = "ione.riead@example.com",
        phoneNumber = "1246264",
        countryCode = "+51",
        gender = GenderUi.FEMALE
    ),
    editForm = ProfileFormState.fromProfile(
        ProfileUi(
            id = "profile-1",
            fullName = "Ione Riead",
            email = "ione.riead@example.com",
            phoneNumber = "1246264",
            countryCode = "+51",
            gender = GenderUi.FEMALE
        )
    )
)

private fun String.isValidEmail(): Boolean =
    isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
package com.paxtech.mobileapp.features.profile.presentation

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.paxtech.mobileapp.features.profile.presentation.model.GenderUi
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentCardBrand
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodFormField
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodFormState
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodUi
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodsUiState
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileFormField
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileFormState
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileUi
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @Named("auth_prefs") private val authPrefs: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _paymentMethodsState = MutableStateFlow(PaymentMethodsUiState())
    val paymentMethodsState: StateFlow<PaymentMethodsUiState> = _paymentMethodsState.asStateFlow()

    init {
        loadProfile()
        loadPaymentMethods()
    }

    fun refreshProfile() {
        loadProfile()
        loadPaymentMethods()
    }

    fun onEditFieldChange(field: ProfileFormField, value: String) {
        _uiState.update { state ->
            val updatedForm = state.editForm.update(field, value)
            state.copy(editForm = updatedForm, errorMessage = null)
        }
    }

    fun onGenderSelected(gender: GenderUi) {
        _uiState.update { state ->
            val updatedForm = state.editForm.updateGender(gender)
            state.copy(editForm = updatedForm, errorMessage = null)
        }
    }

    fun resetEditForm() {
        val profile = _uiState.value.profile ?: return
        _uiState.update { state ->
            state.copy(editForm = ProfileFormState.fromProfile(profile), errorMessage = null)
        }
    }

    fun saveProfile() {
        val currentForm = _uiState.value.editForm
        if (!currentForm.isValid) {
            _uiState.update { it.copy(errorMessage = "Por favor, completa todos los campos requeridos") }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        persistProfile(currentForm)

        val currentProfile = _uiState.value.profile
        val updatedProfile = currentForm.toProfileUi(
            profileId = currentProfile?.id.orEmpty(),
            avatarUrl = currentProfile?.avatarUrl
        )

        _uiState.update {
            it.copy(
                profile = updatedProfile,
                editForm = ProfileFormState.fromProfile(updatedProfile),
                isProfileUpdated = true,
                isSaving = false
            )
        }
    }

    fun onProfileUpdateConsumed() {
        _uiState.update { it.copy(isProfileUpdated = false) }
    }

    fun onPaymentMethodFormChange(field: PaymentMethodFormField, value: String) {
        _paymentMethodsState.update { state ->
            state.copy(
                form = state.form.update(field, value),
                formError = null,
                isSaved = false
            )
        }
    }

    fun prepareNewPaymentMethod() {
        _paymentMethodsState.update {
            it.copy(
                form = PaymentMethodFormState(),
                formError = null,
                editingMethodId = null,
                isSaved = false
            )
        }
    }

    fun prepareEditPaymentMethod(methodId: String) {
        val current = _paymentMethodsState.value.methods.firstOrNull { it.id == methodId } ?: return
        _paymentMethodsState.update {
            it.copy(
                form = PaymentMethodFormState.fromPaymentMethod(current),
                editingMethodId = methodId,
                formError = null,
                isSaved = false
            )
        }
    }

    fun savePaymentMethod() {
        val currentState = _paymentMethodsState.value
        val form = currentState.form
        if (!form.isValid) {
            _paymentMethodsState.update { it.copy(formError = "Completa todos los datos de la tarjeta") }
            return
        }

        val methodId = currentState.editingMethodId ?: UUID.randomUUID().toString()
        val updatedMethod = form.toPaymentMethodUi(methodId)
        val updatedList = if (currentState.editingMethodId != null) {
            currentState.methods.map { existing ->
                if (existing.id == methodId) updatedMethod else existing
            }
        } else {
            currentState.methods + updatedMethod
        }

        persistPaymentMethods(updatedList)

        _paymentMethodsState.update {
            it.copy(
                methods = updatedList,
                form = PaymentMethodFormState(),
                editingMethodId = null,
                formError = null,
                isSaved = true
            )
        }
    }

    fun onPaymentMethodSavedConsumed() {
        _paymentMethodsState.update { it.copy(isSaved = false) }
    }

    fun requestDeletePaymentMethod(method: PaymentMethodUi) {
        _paymentMethodsState.update { it.copy(methodPendingDeletion = method) }
    }

    fun dismissDeletePaymentMethod() {
        _paymentMethodsState.update { it.copy(methodPendingDeletion = null) }
    }

    fun confirmDeletePaymentMethod() {
        val pending = _paymentMethodsState.value.methodPendingDeletion ?: return
        val updatedList = _paymentMethodsState.value.methods.filterNot { it.id == pending.id }
        persistPaymentMethods(updatedList)
        _paymentMethodsState.update {
            it.copy(
                methods = updatedList,
                methodPendingDeletion = null,
                isDeleted = true
            )
        }
    }

    fun onPaymentMethodDeletedConsumed() {
        _paymentMethodsState.update { it.copy(isDeleted = false) }
    }

    fun logout() {
        authPrefs.edit().clear().apply()
        val profile = readProfileFromPreferences()
        _uiState.value = ProfileUiState(
            isLoading = false,
            profile = profile,
            editForm = ProfileFormState.fromProfile(profile),
            isLoggedOut = true
        )
        _paymentMethodsState.value = PaymentMethodsUiState(
            isLoading = false,
            methods = emptyList()
        )
    }

    fun onLogoutHandled() {
        _uiState.update { it.copy(isLoggedOut = false) }
    }

    private fun loadProfile() {
        val profile = readProfileFromPreferences()
        _uiState.update {
            it.copy(
                isLoading = false,
                profile = profile,
                editForm = ProfileFormState.fromProfile(profile),
                errorMessage = null
            )
        }
    }

    private fun loadPaymentMethods() {
        val methods = readPaymentMethodsFromPreferences()
        _paymentMethodsState.update {
            it.copy(
                isLoading = false,
                methods = methods,
                form = PaymentMethodFormState(),
                formError = null,
                editingMethodId = null,
                methodPendingDeletion = null,
                isSaved = false,
                isDeleted = false
            )
        }
    }

    private fun readProfileFromPreferences(): ProfileUi {
        val firstName = authPrefs.getString("user_first_name", "")?.trim().orEmpty()
        val lastName = authPrefs.getString("user_last_name", "")?.trim().orEmpty()
        val fullName = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { "Usuario" }
        val email = authPrefs.getString("user_email", "")?.trim().orEmpty()
        val countryCode = authPrefs.getString("user_country_code", "+51")?.trim().orEmpty().ifBlank { "+51" }
        val phoneNumber = authPrefs.getString("user_phone_number", "")?.trim().orEmpty()
        val genderName = authPrefs.getString("user_gender", GenderUi.default.name)
        val gender = genderName.toGenderUi()
        val avatarUrl = authPrefs.getString("user_avatar_url", null)
        val id = authPrefs.getInt("user_id", 0).takeIf { it != 0 }?.toString() ?: ""

        return ProfileUi(
            id = id,
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            countryCode = countryCode,
            gender = gender,
            avatarUrl = avatarUrl
        )
    }

    private fun persistProfile(form: ProfileFormState) {
        val trimmedName = form.fullName.trim()
        val nameParts = trimmedName.split(" ", limit = 2)
        val firstName = nameParts.getOrNull(0).orEmpty()
        val lastName = nameParts.getOrNull(1).orEmpty()

        authPrefs.edit().apply {
            putString("user_first_name", firstName)
            putString("user_last_name", lastName)
            putString("user_full_name", trimmedName)
            putString("user_email", form.email.trim())
            putString("user_country_code", form.countryCode.trim())
            putString("user_phone_number", form.phoneNumber.trim())
            putString("user_gender", form.gender.name)
        }.apply()
    }

    private fun readPaymentMethodsFromPreferences(): List<PaymentMethodUi> {
        val stored = authPrefs.getString(PAYMENT_METHODS_KEY, null) ?: return emptyList()
        return runCatching {
            val jsonArray = JSONArray(stored)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(index)
                    add(
                        PaymentMethodUi(
                            id = item.getString("id"),
                            cardHolder = item.getString("cardHolder"),
                            cardNumber = item.getString("cardNumber"),
                            expiryMonth = item.getString("expiryMonth"),
                            expiryYear = item.getString("expiryYear"),
                            cvv = item.getString("cvv"),
                            brand = PaymentMethodUiJsonAdapter.brandFromString(item.getString("brand"))
                        )
                    )
                }
            }
        }.getOrElse { emptyList() }
    }

    private fun persistPaymentMethods(methods: List<PaymentMethodUi>) {
        val jsonArray = JSONArray()
        methods.forEach { method ->
            val json = JSONObject().apply {
                put("id", method.id)
                put("cardHolder", method.cardHolder)
                put("cardNumber", method.cardNumber)
                put("expiryMonth", method.expiryMonth)
                put("expiryYear", method.expiryYear)
                put("cvv", method.cvv)
                put("brand", method.brand.name)
            }
            jsonArray.put(json)
        }
        authPrefs.edit().putString(PAYMENT_METHODS_KEY, jsonArray.toString()).apply()
    }

    private object PaymentMethodUiJsonAdapter {
        fun brandFromString(value: String): PaymentCardBrand = runCatching {
            PaymentCardBrand.valueOf(value)
        }.getOrDefault(PaymentCardBrand.UNKNOWN)
    }

    companion object {
        private const val PAYMENT_METHODS_KEY = "user_payment_methods"
    }
}

private fun String?.toGenderUi(): GenderUi {
    if (this.isNullOrBlank()) return GenderUi.default
    return runCatching { GenderUi.valueOf(this) }.getOrDefault(GenderUi.default)
}
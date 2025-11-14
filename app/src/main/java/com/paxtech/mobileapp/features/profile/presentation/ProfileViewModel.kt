package com.paxtech.mobileapp.features.profile.presentation

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paxtech.mobileapp.features.authentication.domain.repository.AuthRepository
import com.paxtech.mobileapp.features.authentication.domain.repository.UserDataRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.LocalSalonRepository
import com.paxtech.mobileapp.features.profile.presentation.model.ChangePasswordField
import java.io.File
import com.paxtech.mobileapp.features.profile.presentation.model.ChangePasswordFormState
import com.paxtech.mobileapp.features.profile.presentation.model.ChangePasswordUiState
import com.paxtech.mobileapp.features.profile.presentation.model.FavoriteSalonUi
import com.paxtech.mobileapp.features.profile.presentation.model.FavoriteSalonsUiState
import com.paxtech.mobileapp.features.profile.presentation.model.GenderUi
import com.paxtech.mobileapp.features.profile.presentation.model.NotificationUi
import com.paxtech.mobileapp.features.profile.presentation.model.NotificationsUiState
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentCardBrand
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodFormField
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodFormState
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodUi
import com.paxtech.mobileapp.features.profile.presentation.model.PaymentMethodsUiState
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileFormField
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileFormState
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileUi
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileUiState
import com.paxtech.mobileapp.features.profile.presentation.model.toFavoriteSalonUi
import com.paxtech.mobileapp.features.profile.presentation.model.toSalon
import com.paxtech.mobileapp.features.profile.presentation.model.toSections
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @param:Named("auth_prefs") private val authPrefs: SharedPreferences,
    private val localSalonRepository: LocalSalonRepository,
    private val authRepository: AuthRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _paymentMethodsState = MutableStateFlow(PaymentMethodsUiState())
    val paymentMethodsState: StateFlow<PaymentMethodsUiState> = _paymentMethodsState.asStateFlow()

    private val _changePasswordState = MutableStateFlow(ChangePasswordUiState())
    val changePasswordState: StateFlow<ChangePasswordUiState> = _changePasswordState.asStateFlow()

    private val _favoriteSalonsState = MutableStateFlow(FavoriteSalonsUiState())
    val favoriteSalonsState: StateFlow<FavoriteSalonsUiState> = _favoriteSalonsState.asStateFlow()

    private val _notificationsState = MutableStateFlow(NotificationsUiState())
    val notificationsState: StateFlow<NotificationsUiState> = _notificationsState.asStateFlow()

    private var storedNotifications: List<NotificationStorage> = emptyList()

    init {
        loadProfile()
        loadPaymentMethods()
        loadPassword()
        refreshFavoriteSalons()
        loadNotifications()
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

    fun refreshFavoriteSalons() {
        _favoriteSalonsState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { localSalonRepository.getAllFavorites() }
                .mapCatching { salons -> salons.map { it.toFavoriteSalonUi() } }
                .onSuccess { favorites ->
                    _favoriteSalonsState.update {
                        it.copy(
                            isLoading = false,
                            salons = favorites,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _favoriteSalonsState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "No pudimos cargar tus salones favoritos"
                        )
                    }
                }
        }
    }

    fun requestRemoveFavorite(salon: FavoriteSalonUi) {
        _favoriteSalonsState.update {
            it.copy(pendingRemoval = salon)
        }
    }

    fun dismissRemoveFavorite() {
        _favoriteSalonsState.update { it.copy(pendingRemoval = null) }
    }

    fun confirmRemoveFavorite() {
        val pending = _favoriteSalonsState.value.pendingRemoval ?: return
        _favoriteSalonsState.update { it.copy(isLoading = true, pendingRemoval = null) }
        viewModelScope.launch {
            runCatching { localSalonRepository.toggleFavorite(pending.toSalon()) }
                .onSuccess {
                    refreshFavoriteSalons()
                }
                .onFailure { throwable ->
                    _favoriteSalonsState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "No pudimos actualizar tus favoritos"
                        )
                    }
                }
        }
    }

    fun refreshNotifications() {
        loadNotifications()
    }

    fun markNotificationAsRead(notification: NotificationUi) {
        if (notification.isRead) return
        storedNotifications = storedNotifications.map { stored ->
            if (stored.id == notification.id) stored.copy(isRead = true) else stored
        }
        persistNotifications(storedNotifications)
        publishNotificationsState(isLoading = false)
    }

    fun clearNotifications() {
        storedNotifications = emptyList()
        persistNotifications(storedNotifications)
        publishNotificationsState(isLoading = false)
    }

    fun toggleNotificationMute() {
        val newValue = !_notificationsState.value.isMuted
        authPrefs.edit().putBoolean(NOTIFICATIONS_MUTED_KEY, newValue).apply()
        _notificationsState.update { it.copy(isMuted = newValue) }
    }

    fun onChangePasswordFieldChange(field: ChangePasswordField, value: String) {
        _changePasswordState.update { state ->
            state.copy(
                form = state.form.update(field, value),
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun changePassword() {
        val storedPassword = authPrefs.getString(USER_PASSWORD_KEY, null)
        val validatedForm = _changePasswordState.value.form.validate(storedPassword)
        if (!validatedForm.isValid) {
            _changePasswordState.update {
                it.copy(
                    form = validatedForm,
                    errorMessage = "Revisa los datos ingresados"
                )
            }
            return
        }

        _changePasswordState.update { it.copy(isSaving = true, errorMessage = null) }

        authPrefs.edit()
            .putString(USER_PASSWORD_KEY, validatedForm.newPassword)
            .apply()

        _changePasswordState.update {
            ChangePasswordUiState(
                form = ChangePasswordFormState.empty(),
                isSaving = false,
                successMessage = "Tu contraseña se actualizó correctamente"
            )
        }
    }

    fun onChangePasswordMessageConsumed() {
        _changePasswordState.update { it.copy(successMessage = null, errorMessage = null) }
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
        _changePasswordState.value = ChangePasswordUiState()
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

    private fun loadPassword() {
        val existingPassword = authPrefs.getString(USER_PASSWORD_KEY, null)
        _changePasswordState.update {
            it.copy(
                form = ChangePasswordFormState.empty(),
                errorMessage = null,
                successMessage = null
            ).let { state ->
                if (!existingPassword.isNullOrEmpty()) state else state
            }
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

    private fun loadNotifications() {
        _notificationsState.update { it.copy(isLoading = true) }
        val stored = readNotificationsFromPreferences()
        storedNotifications = if (stored.isEmpty()) {
            val defaults = defaultNotifications()
            persistNotifications(defaults)
            defaults
        } else {
            stored
        }

        publishNotificationsState(isLoading = false)
    }

    private fun publishNotificationsState(isLoading: Boolean) {
        val notifications = storedNotifications
            .sortedByDescending { it.timestamp }
            .map { it.toUi() }
        _notificationsState.update {
            it.copy(
                isLoading = isLoading,
                sections = notifications.toSections(),
                isMuted = authPrefs.getBoolean(NOTIFICATIONS_MUTED_KEY, false)
            )
        }
    }

    private fun readNotificationsFromPreferences(): List<NotificationStorage> {
        val stored = authPrefs.getString(NOTIFICATIONS_KEY, null) ?: return emptyList()
        return runCatching {
            val jsonArray = JSONArray(stored)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(index)
                    add(
                        NotificationStorage(
                            id = item.getString("id"),
                            title = item.getString("title"),
                            message = item.getString("message"),
                            timestamp = item.getLong("timestamp"),
                            isRead = item.getBoolean("isRead"),
                            avatarUrl = item.optString("avatarUrl").ifBlank { null }
                        )
                    )
                }
            }
        }.getOrElse { emptyList() }
    }

    private fun persistNotifications(notifications: List<NotificationStorage>) {
        val jsonArray = JSONArray()
        notifications.forEach { notification ->
            val json = JSONObject().apply {
                put("id", notification.id)
                put("title", notification.title)
                put("message", notification.message)
                put("timestamp", notification.timestamp)
                put("isRead", notification.isRead)
                put("avatarUrl", notification.avatarUrl)
            }
            jsonArray.put(json)
        }
        authPrefs.edit().putString(NOTIFICATIONS_KEY, jsonArray.toString()).apply()
    }

    private fun defaultNotifications(): List<NotificationStorage> {
        val now = System.currentTimeMillis()
        return listOf(
            NotificationStorage(
                id = "notification-1",
                title = "Tu pedido está en camino",
                message = "La estilista llegará en 30 minutos.",
                timestamp = now - 15 * 60 * 1000,
                isRead = false,
                avatarUrl = null
            ),
            NotificationStorage(
                id = "notification-2",
                title = "Recordatorio de reserva",
                message = "No olvides tu cita en Glamour Studio mañana a las 10:00 a.m.",
                timestamp = now - 3 * 60 * 60 * 1000,
                isRead = false,
                avatarUrl = null
            ),
            NotificationStorage(
                id = "notification-3",
                title = "Nuevas ofertas especiales",
                message = "Descubre los descuentos exclusivos para esta semana.",
                timestamp = now - 26 * 60 * 60 * 1000,
                isRead = true,
                avatarUrl = null
            ),
            NotificationStorage(
                id = "notification-4",
                title = "Estilista favorito disponible",
                message = "Cameron Williamson abrió espacios para el viernes.",
                timestamp = now - 30 * 60 * 60 * 1000,
                isRead = true,
                avatarUrl = null
            )
        )
    }

    fun uploadProfileImage(imageFile: File) {
        val clientId = userDataRepository.getClientId()
        if (clientId == 0) {
            _uiState.update { 
                it.copy(errorMessage = "No se pudo obtener el ID del cliente") 
            }
            return
        }
        
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        
        viewModelScope.launch {
            authRepository.uploadClientProfileImage(clientId, imageFile)
                .onSuccess {
                    // Recargar el perfil para obtener la nueva URL
                    refreshClientProfile()
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            errorMessage = "Error al subir la imagen: ${e.message}"
                        ) 
                    }
                }
        }
    }
    
    private fun refreshClientProfile() {
        val userId = userDataRepository.getUserId()
        if (userId == 0) {
            _uiState.update { 
                it.copy(
                    isSaving = false,
                    errorMessage = "No se pudo obtener el ID del usuario"
                ) 
            }
            return
        }
        
        viewModelScope.launch {
            authRepository.getClientByUserId(userId)
                ?.let { client ->
                    // Actualizar el avatar URL en SharedPreferences
                    authPrefs.edit()
                        .putString("user_avatar_url", client.profileImageUrl)
                        .apply()
                    
                    // Recargar el perfil
                    loadProfile()
                    _uiState.update { it.copy(isSaving = false) }
                } ?: run {
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            errorMessage = "No se pudo actualizar el perfil"
                        ) 
                    }
                }
        }
    }

    private object PaymentMethodUiJsonAdapter {
        fun brandFromString(value: String): PaymentCardBrand = runCatching {
            PaymentCardBrand.valueOf(value)
        }.getOrDefault(PaymentCardBrand.UNKNOWN)
    }

    companion object {
        private const val PAYMENT_METHODS_KEY = "user_payment_methods"
        private const val USER_PASSWORD_KEY = "user_password"
        private const val NOTIFICATIONS_KEY = "user_notifications"
        private const val NOTIFICATIONS_MUTED_KEY = "user_notifications_muted"
    }
}

private fun String?.toGenderUi(): GenderUi {
    if (this.isNullOrBlank()) return GenderUi.default
    return runCatching { GenderUi.valueOf(this) }.getOrDefault(GenderUi.default)
}

private data class NotificationStorage(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val avatarUrl: String?
) {
    fun toUi(): NotificationUi = NotificationUi(
        id = id,
        title = title,
        message = message,
        timestamp = timestamp,
        isRead = isRead,
        avatarUrl = avatarUrl
    )
}
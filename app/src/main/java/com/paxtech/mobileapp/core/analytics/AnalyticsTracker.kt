package com.paxtech.mobileapp.core.analytics

import com.paxtech.mobileapp.features.authentication.domain.repository.UserDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registra eventos de analítica (UE01-UE05) en el backend.
 *
 * Es "fire-and-forget": corre en su propio scope de IO y traga cualquier error,
 * de modo que el tracking **nunca** bloquea la UI ni rompe la app. El actor se
 * resuelve del clientId guardado en sesión.
 */
@Singleton
class AnalyticsTracker @Inject constructor(
    private val service: AnalyticsService,
    private val userDataRepository: UserDataRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun track(
        eventType: String,
        providerId: Long? = null,
        reservationId: Long? = null,
        metadata: Map<String, String> = emptyMap()
    ) {
        scope.launch {
            try {
                val clientId = runCatching { userDataRepository.getClientId() }.getOrNull() ?: 0
                service.track(
                    AnalyticsEventDto(
                        eventType = eventType,
                        actorType = if (clientId > 0) "CLIENT" else "ANONYMOUS",
                        actorId = if (clientId > 0) clientId.toLong() else null,
                        providerId = providerId,
                        reservationId = reservationId,
                        metadata = metadata.ifEmpty { null }
                    )
                )
            } catch (_: Exception) {
                // El tracking nunca debe romper la app.
            }
        }
    }

    // --- Atajos por evento del To-Be backlog ---

    /** UE05 / WI20 — el usuario abrió el perfil de un salón. */
    fun profileView(providerId: Long, hasReviews: Boolean? = null, hasPortfolio: Boolean? = null) {
        val md = buildMap {
            hasReviews?.let { put("has_reviews", it.toString()) }
            hasPortfolio?.let { put("has_portfolio", it.toString()) }
        }
        track("profile_view", providerId = providerId, metadata = md)
    }

    /** UE04 / WI15 — el usuario inició una reserva dentro de la app. */
    fun bookInAppStarted(providerId: Long?) =
        track("book_in_app_started", providerId = providerId)

    /** UE04 / WI15 — la reserva en app se completó. */
    fun bookInAppCompleted(providerId: Long?, reservationId: Long?) =
        track("book_in_app_completed", providerId = providerId, reservationId = reservationId)

    /** UE03 / WI12 — el usuario inició el pago. */
    fun paymentStarted(reservationId: Long?, method: String = "stripe") =
        track("payment_started", reservationId = reservationId, metadata = mapOf("method" to method))

    /** UE03 / WI12 — el pago se completó. */
    fun paymentCompleted(reservationId: Long?, method: String = "stripe") =
        track("payment_completed", reservationId = reservationId, metadata = mapOf("method" to method))

    /** UE04 / WI15 — el usuario contactó por WhatsApp (canal externo). */
    fun contactWhatsapp(providerId: Long?) =
        track("contact_whatsapp_initiated", providerId = providerId)
}

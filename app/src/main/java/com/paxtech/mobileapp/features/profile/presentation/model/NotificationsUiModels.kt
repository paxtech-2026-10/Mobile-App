package com.paxtech.mobileapp.features.profile.presentation.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * UI model for a single notification.
 */
data class NotificationUi(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val avatarUrl: String?
) {
    val relativeTime: String
        get() = timestamp.toRelativeTime()
}

/**
 * A section of notifications grouped by date label (e.g. "Hoy", "Ayer").
 */
data class NotificationSectionUi(
    val title: String,
    val notifications: List<NotificationUi>
)

/**
 * UI state for the notifications screen.
 */
data class NotificationsUiState(
    val isLoading: Boolean = true,
    val sections: List<NotificationSectionUi> = emptyList(),
    val isMuted: Boolean = false
) {
    val hasNotifications: Boolean get() = sections.any { it.notifications.isNotEmpty() }
    val unreadCount: Int get() = sections.sumOf { section -> section.notifications.count { !it.isRead } }
}

private const val MINUTE_MILLIS = 60_000L
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS

private fun Long.toRelativeTime(now: Long = System.currentTimeMillis()): String {
    val diff = now - this
    return when {
        diff < MINUTE_MILLIS -> "Justo ahora"
        diff < HOUR_MILLIS -> "Hace ${diff / MINUTE_MILLIS} min"
        diff < DAY_MILLIS -> "Hace ${diff / HOUR_MILLIS} h"
        diff < 7 * DAY_MILLIS -> "Hace ${diff / DAY_MILLIS} d"
        else -> {
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            formatter.format(Date(this))
        }
    }
}

internal fun List<NotificationUi>.toSections(now: Long = System.currentTimeMillis()): List<NotificationSectionUi> {
    if (isEmpty()) return emptyList()

    val grouped = mutableMapOf<String, MutableList<NotificationUi>>()
    for (notification in this.sortedByDescending { it.timestamp }) {
        val title = notification.timestamp.toSectionTitle(now)
        grouped.getOrPut(title) { mutableListOf() }.add(notification)
    }

    return grouped.entries
        .sortedBy { entry -> entry.value.minOf { it.timestamp } }
        .map { (title, notifications) ->
            NotificationSectionUi(title = title, notifications = notifications)
        }
        .reversed()
}

private fun Long.toSectionTitle(now: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = now }
    val todayStart = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val yesterdayStart = todayStart - DAY_MILLIS

    return when {
        this >= todayStart -> "Hoy"
        this >= yesterdayStart -> "Ayer"
        else -> {
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            formatter.format(Date(this))
        }
    }
}
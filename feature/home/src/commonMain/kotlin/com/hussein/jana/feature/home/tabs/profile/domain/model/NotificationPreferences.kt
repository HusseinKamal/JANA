package com.hussein.jana.feature.home.tabs.profile.domain.model

/** Notification categories the student can switch on or off individually. */
enum class NotificationType {
    LIVE_LESSON_REMINDERS,
    EXAM_REMINDERS,
    NEW_MESSAGES,
    FEEDBACK,
    SUBSCRIPTION_EXPIRY,
}

/**
 * [enabled] is the master switch. Turning it off keeps the per-type values so they come back
 * unchanged when it is turned on again.
 */
data class NotificationPreferences(
    val enabled: Boolean = true,
    val liveLessonReminders: Boolean = true,
    val examReminders: Boolean = true,
    val newMessages: Boolean = true,
    val feedback: Boolean = true,
    val subscriptionExpiry: Boolean = true,
) {
    fun isEnabled(type: NotificationType): Boolean = when (type) {
        NotificationType.LIVE_LESSON_REMINDERS -> liveLessonReminders
        NotificationType.EXAM_REMINDERS -> examReminders
        NotificationType.NEW_MESSAGES -> newMessages
        NotificationType.FEEDBACK -> feedback
        NotificationType.SUBSCRIPTION_EXPIRY -> subscriptionExpiry
    }

    fun with(type: NotificationType, enabled: Boolean): NotificationPreferences = when (type) {
        NotificationType.LIVE_LESSON_REMINDERS -> copy(liveLessonReminders = enabled)
        NotificationType.EXAM_REMINDERS -> copy(examReminders = enabled)
        NotificationType.NEW_MESSAGES -> copy(newMessages = enabled)
        NotificationType.FEEDBACK -> copy(feedback = enabled)
        NotificationType.SUBSCRIPTION_EXPIRY -> copy(subscriptionExpiry = enabled)
    }
}

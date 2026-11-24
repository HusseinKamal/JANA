package com.hussein.jana.feature.home.tabs.profile.presentation.profile.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.hussein.jana.core.designsystem.JanaSettingsSection
import com.hussein.jana.core.designsystem.JanaSwitchRow
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.profile_notif_exam_reminders
import com.hussein.jana.core.designsystem.profile_notif_feedback
import com.hussein.jana.core.designsystem.profile_notif_live_reminders
import com.hussein.jana.core.designsystem.profile_notif_new_messages
import com.hussein.jana.core.designsystem.profile_notif_subscription_expiry
import com.hussein.jana.core.designsystem.profile_notifications_allow
import com.hussein.jana.core.designsystem.profile_notifications_allow_subtitle
import com.hussein.jana.core.designsystem.profile_section_notifications
import com.hussein.jana.feature.home.tabs.profile.domain.model.NotificationPreferences
import com.hussein.jana.feature.home.tabs.profile.domain.model.NotificationType
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Master "Allow notifications" switch followed by one switch per [NotificationType].
 * The per-type switches are disabled (but keep their values) while the master switch is off.
 */
@Composable
fun NotificationSettingsSection(
    preferences: NotificationPreferences,
    onEnabledChange: (Boolean) -> Unit,
    onTypeChange: (NotificationType, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val types = NotificationType.entries
    JanaSettingsSection(
        title = stringResource(Res.string.profile_section_notifications),
        modifier = modifier,
    ) {
        JanaSwitchRow(
            title = stringResource(Res.string.profile_notifications_allow),
            subtitle = stringResource(Res.string.profile_notifications_allow_subtitle),
            icon = Icons.Rounded.Notifications,
            checked = preferences.enabled,
            onCheckedChange = onEnabledChange,
            showDivider = true,
        )
        types.forEachIndexed { index, type ->
            JanaSwitchRow(
                title = stringResource(type.titleRes()),
                icon = type.icon(),
                checked = preferences.isEnabled(type),
                onCheckedChange = { onTypeChange(type, it) },
                enabled = preferences.enabled,
                showDivider = index < types.lastIndex,
            )
        }
    }
}

private fun NotificationType.titleRes(): StringResource = when (this) {
    NotificationType.LIVE_LESSON_REMINDERS -> Res.string.profile_notif_live_reminders
    NotificationType.EXAM_REMINDERS -> Res.string.profile_notif_exam_reminders
    NotificationType.NEW_MESSAGES -> Res.string.profile_notif_new_messages
    NotificationType.FEEDBACK -> Res.string.profile_notif_feedback
    NotificationType.SUBSCRIPTION_EXPIRY -> Res.string.profile_notif_subscription_expiry
}

private fun NotificationType.icon(): ImageVector = when (this) {
    NotificationType.LIVE_LESSON_REMINDERS -> Icons.Rounded.Videocam
    NotificationType.EXAM_REMINDERS -> Icons.Rounded.Quiz
    NotificationType.NEW_MESSAGES -> Icons.AutoMirrored.Rounded.Chat
    NotificationType.FEEDBACK -> Icons.Rounded.RateReview
    NotificationType.SUBSCRIPTION_EXPIRY -> Icons.Rounded.EventBusy
}

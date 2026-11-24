package com.hussein.jana.feature.home.tabs.profile.presentation.common

import androidx.compose.runtime.Composable
import com.hussein.jana.core.designsystem.AppLanguage
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.common_percent
import com.hussein.jana.core.designsystem.error_invalid_email
import com.hussein.jana.core.designsystem.error_name_required
import com.hussein.jana.core.designsystem.error_name_too_short
import com.hussein.jana.core.designsystem.error_phone_invalid
import com.hussein.jana.core.designsystem.error_phone_required
import com.hussein.jana.core.designsystem.language_arabic
import com.hussein.jana.core.designsystem.language_english
import com.hussein.jana.core.designsystem.profile_progress_lessons
import com.hussein.jana.core.designsystem.subscription_status_active
import com.hussein.jana.core.designsystem.subscription_status_expired
import com.hussein.jana.core.designsystem.subscription_status_not_subscribed
import com.hussein.jana.feature.home.tabs.profile.domain.model.ProfileFieldError
import com.hussein.jana.feature.home.tabs.teacher.domain.model.SubscriptionStatus
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** 0.42f -> "42%" (localized percent sign). */
@Composable
internal fun Float.percentText(): String =
    stringResource(Res.string.common_percent, (coerceIn(0f, 1f) * 100).roundToInt())

@Composable
internal fun lessonsProgressText(completed: Int, total: Int): String =
    stringResource(Res.string.profile_progress_lessons, completed, total)

@Composable
internal fun AppLanguage.label(): String = when (this) {
    AppLanguage.ENGLISH -> stringResource(Res.string.language_english)
    AppLanguage.ARABIC -> stringResource(Res.string.language_arabic)
}

@Composable
internal fun SubscriptionStatus.label(): String = when (this) {
    SubscriptionStatus.ACTIVE -> stringResource(Res.string.subscription_status_active)
    SubscriptionStatus.EXPIRED -> stringResource(Res.string.subscription_status_expired)
    SubscriptionStatus.NOT_SUBSCRIBED -> stringResource(Res.string.subscription_status_not_subscribed)
}

@Composable
internal fun ProfileFieldError.message(): String = when (this) {
    ProfileFieldError.NAME_REQUIRED -> stringResource(Res.string.error_name_required)
    ProfileFieldError.NAME_TOO_SHORT -> stringResource(Res.string.error_name_too_short)
    ProfileFieldError.EMAIL_INVALID -> stringResource(Res.string.error_invalid_email)
    ProfileFieldError.PHONE_REQUIRED -> stringResource(Res.string.error_phone_required)
    ProfileFieldError.PHONE_INVALID -> stringResource(Res.string.error_phone_invalid)
}

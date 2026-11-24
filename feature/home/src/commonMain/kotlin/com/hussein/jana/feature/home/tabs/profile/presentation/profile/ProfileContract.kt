package com.hussein.jana.feature.home.tabs.profile.presentation.profile

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.core.designsystem.AppLanguage
import com.hussein.jana.feature.home.tabs.profile.domain.model.LearningProgress
import com.hussein.jana.feature.home.tabs.profile.domain.model.NotificationPreferences
import com.hussein.jana.feature.home.tabs.profile.domain.model.NotificationType
import com.hussein.jana.feature.home.tabs.profile.domain.model.StudentProfile
import com.hussein.jana.feature.home.tabs.teacher.domain.model.SubscriptionStatus
import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherProfile

/**
 * Each section loads independently, so one failing section does not blank the whole tab.
 * Errors are flags only; the screen picks the localized text.
 */
data class ProfileUiState(
    val isProfileLoading: Boolean = true,
    val profile: StudentProfile? = null,
    val profileError: Boolean = false,
    val isProgressLoading: Boolean = true,
    val progress: LearningProgress? = null,
    val progressError: Boolean = false,
    /** Only the active teacher platform; null while loading or when it failed to load. */
    val teacher: TeacherProfile? = null,
    val subscribedCoursesCount: Int? = null,
    val liveSubscriptionStatus: SubscriptionStatus? = null,
    val notifications: NotificationPreferences = NotificationPreferences(),
    val language: AppLanguage = AppLanguage.ENGLISH,
    val isLanguageSheetVisible: Boolean = false,
    val isLogoutDialogVisible: Boolean = false,
    val isLoggingOut: Boolean = false,
    val appVersion: String = "",
) : MviState

sealed interface ProfileIntent : MviIntent {
    data object Load : ProfileIntent
    /** The tab entered composition; refreshes data that has no observable source. */
    data object ScreenShown : ProfileIntent
    data object RetryProfile : ProfileIntent
    data object RetryProgress : ProfileIntent
    data object EditProfileClicked : ProfileIntent
    data object SyllabusClicked : ProfileIntent
    data object ProgressClicked : ProfileIntent
    data class NotificationsToggled(val enabled: Boolean) : ProfileIntent
    data class NotificationTypeToggled(val type: NotificationType, val enabled: Boolean) : ProfileIntent
    data object LanguageRowClicked : ProfileIntent
    data class LanguageSelected(val language: AppLanguage) : ProfileIntent
    data object DismissLanguageSheet : ProfileIntent
    data object TeacherPlatformClicked : ProfileIntent
    data object SubscriptionsClicked : ProfileIntent
    data object LogoutClicked : ProfileIntent
    data object ConfirmLogout : ProfileIntent
    data object DismissLogoutDialog : ProfileIntent
}

sealed interface ProfileEffect : MviEffect {
    data object NavigateToEditProfile : ProfileEffect
    data object NavigateToSyllabus : ProfileEffect
    data object NavigateToProgress : ProfileEffect
    data object OpenTeacherTab : ProfileEffect
    data object NavigateToLogin : ProfileEffect
    data object ShowError : ProfileEffect
}

package com.hussein.jana.feature.home.tabs.profile.presentation.profile

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.appVersion
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.core.designsystem.AppLanguage
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCoursesUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.model.NotificationPreferences
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.GetLearningProgressUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.GetStudentProfileUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.LogoutUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.ObserveAppLanguageUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.ObserveNotificationPreferencesUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.ObserveStudentProfileUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.SetAppLanguageUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.UpdateNotificationPreferencesUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetActiveTeacherProfileUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetLiveLessonsSubscriptionUseCase
import kotlinx.coroutines.launch

/**
 * Student Profile tab: profile header, learning progress, notification switches, language,
 * the active teacher platform and logout.
 *
 * Switching the language needs no effect: the app root re-provides the locale and this
 * ViewModel survives the recomposition, so the student stays on the Profile tab.
 */
class ProfileViewModel(
    private val getStudentProfile: GetStudentProfileUseCase,
    private val observeStudentProfile: ObserveStudentProfileUseCase,
    private val getLearningProgress: GetLearningProgressUseCase,
    private val getActiveTeacherProfile: GetActiveTeacherProfileUseCase,
    private val getCourses: GetCoursesUseCase,
    private val getLiveLessonsSubscription: GetLiveLessonsSubscriptionUseCase,
    private val observeAppLanguage: ObserveAppLanguageUseCase,
    private val setAppLanguage: SetAppLanguageUseCase,
    private val observeNotificationPreferences: ObserveNotificationPreferencesUseCase,
    private val updateNotificationPreferences: UpdateNotificationPreferencesUseCase,
    private val logout: LogoutUseCase,
) : BaseMviViewModel<ProfileUiState, ProfileIntent, ProfileEffect>(
    ProfileUiState(appVersion = appVersion),
) {

    init {
        onIntent(ProfileIntent.Load)
        // Edits saved in Edit Profile; applied only after the first load so the spinner shows.
        viewModelScope.launch {
            observeStudentProfile().collect { profile ->
                if (!currentState().isProfileLoading) setState { copy(profile = profile) }
            }
        }
        viewModelScope.launch {
            observeAppLanguage().collect { setState { copy(language = it) } }
        }
        viewModelScope.launch {
            observeNotificationPreferences().collect { setState { copy(notifications = it) } }
        }
    }

    override fun onIntent(intent: ProfileIntent) = when (intent) {
        ProfileIntent.Load -> {
            loadProfile()
            loadProgress()
            loadTeacherPlatform()
        }
        ProfileIntent.RetryProfile -> loadProfile()
        ProfileIntent.RetryProgress -> loadProgress()
        ProfileIntent.EditProfileClicked -> sendEffect(ProfileEffect.NavigateToEditProfile)
        ProfileIntent.SyllabusClicked -> sendEffect(ProfileEffect.NavigateToSyllabus)
        ProfileIntent.ProgressClicked -> sendEffect(ProfileEffect.NavigateToProgress)
        is ProfileIntent.CourseClicked -> sendEffect(ProfileEffect.NavigateToCourseDetail(intent.courseId))
        // TODO: request the OS notification permission (POST_NOTIFICATIONS on Android,
        //  UNUserNotificationCenter on iOS) when enabling; no expect/actual exists yet.
        is ProfileIntent.NotificationsToggled ->
            saveNotifications(currentState().notifications.copy(enabled = intent.enabled))
        is ProfileIntent.NotificationTypeToggled ->
            saveNotifications(currentState().notifications.with(intent.type, intent.enabled))
        ProfileIntent.LanguageRowClicked -> setState { copy(isLanguageSheetVisible = true) }
        is ProfileIntent.LanguageSelected -> selectLanguage(intent.language)
        ProfileIntent.DismissLanguageSheet -> setState { copy(isLanguageSheetVisible = false) }
        // TODO: multi-account switching between saved teacher platforms. Today the student
        //  sees only the current teacher, and this opens that teacher's tab.
        ProfileIntent.TeacherPlatformClicked -> sendEffect(ProfileEffect.OpenTeacherTab)
        ProfileIntent.SubscriptionsClicked -> sendEffect(ProfileEffect.OpenTeacherTab)
        ProfileIntent.LogoutClicked -> setState { copy(isLogoutDialogVisible = true) }
        ProfileIntent.ConfirmLogout -> confirmLogout()
        ProfileIntent.DismissLogoutDialog -> dismissLogoutDialog()
    }

    // ── Section loaders ──────────────────────────────────────────────────────

    private fun loadProfile() {
        setState { copy(isProfileLoading = true, profileError = false) }
        viewModelScope.launch {
            getStudentProfile()
                .onSuccess { setState { copy(isProfileLoading = false, profile = it) } }
                .onFailure { setState { copy(isProfileLoading = false, profileError = true) } }
        }
    }

    private fun loadProgress() {
        setState { copy(isProgressLoading = true, progressError = false) }
        viewModelScope.launch {
            getLearningProgress()
                .onSuccess { setState { copy(isProgressLoading = false, progress = it) } }
                .onFailure { setState { copy(isProgressLoading = false, progressError = true) } }
        }
    }

    /** Teacher, course count and live subscription are optional rows; failures just hide details. */
    private fun loadTeacherPlatform() {
        viewModelScope.launch {
            getActiveTeacherProfile().onSuccess { setState { copy(teacher = it) } }
        }
        viewModelScope.launch {
            getCourses().onSuccess { courses ->
                setState { copy(subscribedCoursesCount = courses.count { it.isSubscribed }) }
            }
        }
        viewModelScope.launch {
            getLiveLessonsSubscription().onSuccess {
                setState { copy(liveSubscriptionStatus = it.status) }
            }
        }
    }

    // ── Settings ─────────────────────────────────────────────────────────────

    private fun saveNotifications(preferences: NotificationPreferences) {
        setState { copy(notifications = preferences) }
        viewModelScope.launch { updateNotificationPreferences(preferences) }
    }

    private fun selectLanguage(language: AppLanguage) {
        setState { copy(isLanguageSheetVisible = false) }
        viewModelScope.launch { setAppLanguage(language) }
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    /** Ignored while logging out, so the dialog keeps showing the progress. */
    private fun dismissLogoutDialog() {
        if (!currentState().isLoggingOut) setState { copy(isLogoutDialogVisible = false) }
    }

    private fun confirmLogout() {
        if (currentState().isLoggingOut) return
        setState { copy(isLoggingOut = true) }
        viewModelScope.launch {
            logout()
                .onSuccess {
                    setState { copy(isLoggingOut = false, isLogoutDialogVisible = false) }
                    sendEffect(ProfileEffect.NavigateToLogin)
                }
                .onFailure {
                    setState { copy(isLoggingOut = false, isLogoutDialogVisible = false) }
                    sendEffect(ProfileEffect.ShowError)
                }
        }
    }
}

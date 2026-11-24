package com.hussein.jana.feature.home.tabs.profile.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.CardMembership
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AppLanguage
import com.hussein.jana.core.designsystem.JanaConfirmDialog
import com.hussein.jana.core.designsystem.JanaErrorState
import com.hussein.jana.core.designsystem.JanaLoadingState
import com.hussein.jana.core.designsystem.JanaSelectionOption
import com.hussein.jana.core.designsystem.JanaSelectionSheet
import com.hussein.jana.core.designsystem.JanaSettingsRow
import com.hussein.jana.core.designsystem.JanaSettingsSection
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.btn_cancel
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.error_generic
import com.hussein.jana.core.designsystem.profile_app_version
import com.hussein.jana.core.designsystem.profile_language_sheet_title
import com.hussein.jana.core.designsystem.profile_logout
import com.hussein.jana.core.designsystem.profile_logout_message
import com.hussein.jana.core.designsystem.profile_logout_title
import com.hussein.jana.core.designsystem.profile_platform_current
import com.hussein.jana.core.designsystem.profile_progress
import com.hussein.jana.core.designsystem.profile_progress_view_details
import com.hussein.jana.core.designsystem.profile_section_about
import com.hussein.jana.core.designsystem.profile_section_learning
import com.hussein.jana.core.designsystem.profile_section_platform
import com.hussein.jana.core.designsystem.profile_section_preferences
import com.hussein.jana.core.designsystem.profile_subscriptions
import com.hussein.jana.core.designsystem.profile_subscriptions_subtitle
import com.hussein.jana.core.designsystem.profile_syllabus
import com.hussein.jana.core.designsystem.profile_syllabus_subtitle
import com.hussein.jana.core.designsystem.settings_language
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.AuthRoute
import com.hussein.jana.core.navigation.HomeRoute
import com.hussein.jana.feature.home.common.teacherTitleWithSubject
import com.hussein.jana.feature.home.tabs.profile.presentation.common.label
import com.hussein.jana.feature.home.tabs.profile.presentation.profile.components.NotificationSettingsSection
import com.hussein.jana.feature.home.tabs.profile.presentation.profile.components.ProfileHeaderCard
import com.hussein.jana.feature.home.tabs.profile.presentation.profile.components.ProgressSummaryCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// ── Entry (stateful) ──────────────────────────────────────────────────────────

/**
 * Student Profile tab. [onOpenTeacherTab] switches Home to the active teacher's tab, which
 * already shows that teacher's courses, live subscription and chat.
 */
@Composable
fun ProfileScreen(
    onOpenTeacherTab: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
    navigator: AppNavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorText = stringResource(Res.string.error_generic)

    LaunchedEffect(Unit) { viewModel.onIntent(ProfileIntent.ScreenShown) }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileEffect.NavigateToEditProfile -> navigator.navigate(HomeRoute.EditProfile)
                ProfileEffect.NavigateToSyllabus -> navigator.navigate(HomeRoute.Syllabus)
                ProfileEffect.NavigateToProgress -> navigator.navigate(HomeRoute.Progress)
                ProfileEffect.OpenTeacherTab -> onOpenTeacherTab()
                ProfileEffect.NavigateToLogin -> navigator.replaceAll(AuthRoute.Login())
                ProfileEffect.ShowError -> snackbarHostState.showSnackbar(errorText)
            }
        }
    }

    ProfileContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
fun ProfileContent(
    state: ProfileUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (ProfileIntent) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // Nested inside HomeScreen's Scaffold, which already handles system insets.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            headerSection(state, onIntent)
            learningSection(state, onIntent)
            notificationsSection(state, onIntent)
            preferencesSection(state, onIntent)
            teacherPlatformSection(state, onIntent)
            aboutSection(state)
            logoutSection(onIntent)
        }
    }

    if (state.isLanguageSheetVisible) {
        LanguageSheet(
            selected = state.language,
            onSelect = { onIntent(ProfileIntent.LanguageSelected(it)) },
            onDismiss = { onIntent(ProfileIntent.DismissLanguageSheet) },
        )
    }

    if (state.isLogoutDialogVisible) {
        JanaConfirmDialog(
            title = stringResource(Res.string.profile_logout_title),
            message = stringResource(Res.string.profile_logout_message),
            confirmLabel = stringResource(Res.string.profile_logout),
            dismissLabel = stringResource(Res.string.btn_cancel),
            onConfirm = { onIntent(ProfileIntent.ConfirmLogout) },
            onDismiss = { onIntent(ProfileIntent.DismissLogoutDialog) },
            isDestructive = true,
            isConfirmLoading = state.isLoggingOut,
        )
    }
}

// ── Sections (each a small LazyListScope block) ───────────────────────────────

private fun LazyListScope.headerSection(
    state: ProfileUiState,
    onIntent: (ProfileIntent) -> Unit,
) {
    item(key = "header") {
        val profile = state.profile
        when {
            state.isProfileLoading -> JanaLoadingState()
            state.profileError || profile == null -> JanaErrorState(
                message = stringResource(Res.string.error_generic),
                retryLabel = stringResource(Res.string.btn_retry),
                onRetry = { onIntent(ProfileIntent.RetryProfile) },
            )
            else -> ProfileHeaderCard(
                name = profile.fullName,
                email = profile.email,
                phone = profile.phone,
                photoUrl = profile.photoUrl,
                onEditClick = { onIntent(ProfileIntent.EditProfileClicked) },
            )
        }
    }
}

private fun LazyListScope.learningSection(
    state: ProfileUiState,
    onIntent: (ProfileIntent) -> Unit,
) {
    item(key = "learning") {
        JanaSettingsSection(title = stringResource(Res.string.profile_section_learning)) {
            ProgressSummaryCard(
                isLoading = state.isProgressLoading,
                isError = state.progressError,
                progress = state.progress,
                onRetry = { onIntent(ProfileIntent.RetryProgress) },
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            JanaSettingsRow(
                title = stringResource(Res.string.profile_progress),
                subtitle = stringResource(Res.string.profile_progress_view_details),
                icon = Icons.Rounded.Insights,
                onClick = { onIntent(ProfileIntent.ProgressClicked) },
                showDivider = true,
            )
            JanaSettingsRow(
                title = stringResource(Res.string.profile_syllabus),
                subtitle = stringResource(Res.string.profile_syllabus_subtitle),
                icon = Icons.AutoMirrored.Rounded.MenuBook,
                onClick = { onIntent(ProfileIntent.SyllabusClicked) },
            )
        }
    }
}

private fun LazyListScope.notificationsSection(
    state: ProfileUiState,
    onIntent: (ProfileIntent) -> Unit,
) {
    item(key = "notifications") {
        NotificationSettingsSection(
            preferences = state.notifications,
            onEnabledChange = { onIntent(ProfileIntent.NotificationsToggled(it)) },
            onTypeChange = { type, enabled -> onIntent(ProfileIntent.NotificationTypeToggled(type, enabled)) },
        )
    }
}

private fun LazyListScope.preferencesSection(
    state: ProfileUiState,
    onIntent: (ProfileIntent) -> Unit,
) {
    item(key = "preferences") {
        JanaSettingsSection(title = stringResource(Res.string.profile_section_preferences)) {
            JanaSettingsRow(
                title = stringResource(Res.string.settings_language),
                icon = Icons.Rounded.Language,
                value = state.language.label(),
                onClick = { onIntent(ProfileIntent.LanguageRowClicked) },
            )
        }
    }
}

/** Only the active teacher platform is shown; there is no list of other teachers. */
private fun LazyListScope.teacherPlatformSection(
    state: ProfileUiState,
    onIntent: (ProfileIntent) -> Unit,
) {
    item(key = "teacher_platform") {
        val teacher = state.teacher
        val coursesCount = state.subscribedCoursesCount
        val liveStatus = state.liveSubscriptionStatus
        JanaSettingsSection(title = stringResource(Res.string.profile_section_platform)) {
            JanaSettingsRow(
                title = stringResource(Res.string.profile_platform_current),
                subtitle = teacher?.let { teacherTitleWithSubject(it.name, it.gender, it.subject) },
                icon = Icons.Rounded.School,
                onClick = { onIntent(ProfileIntent.TeacherPlatformClicked) },
                showDivider = true,
            )
            JanaSettingsRow(
                title = stringResource(Res.string.profile_subscriptions),
                subtitle = if (coursesCount != null && liveStatus != null) {
                    stringResource(Res.string.profile_subscriptions_subtitle, coursesCount, liveStatus.label())
                } else {
                    null
                },
                icon = Icons.Rounded.CardMembership,
                onClick = { onIntent(ProfileIntent.SubscriptionsClicked) },
            )
        }
    }
}

private fun LazyListScope.aboutSection(state: ProfileUiState) {
    item(key = "about") {
        JanaSettingsSection(title = stringResource(Res.string.profile_section_about)) {
            JanaSettingsRow(
                title = stringResource(Res.string.profile_app_version),
                icon = Icons.Rounded.Info,
                value = state.appVersion,
                onClick = null,
            )
        }
    }
}

private fun LazyListScope.logoutSection(onIntent: (ProfileIntent) -> Unit) {
    item(key = "logout") {
        JanaSettingsSection(title = null) {
            JanaSettingsRow(
                title = stringResource(Res.string.profile_logout),
                icon = Icons.AutoMirrored.Rounded.Logout,
                onClick = { onIntent(ProfileIntent.LogoutClicked) },
                showChevron = false,
                isDestructive = true,
            )
        }
    }
}

// ── Language sheet ────────────────────────────────────────────────────────────

@Composable
private fun LanguageSheet(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    JanaSelectionSheet(
        title = stringResource(Res.string.profile_language_sheet_title),
        options = AppLanguage.entries.map { JanaSelectionOption(value = it, label = it.label()) },
        selected = selected,
        onSelect = onSelect,
        onDismissRequest = onDismiss,
    )
}

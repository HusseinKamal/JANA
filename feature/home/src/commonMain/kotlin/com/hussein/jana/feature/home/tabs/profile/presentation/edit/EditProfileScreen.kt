package com.hussein.jana.feature.home.tabs.profile.presentation.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaBackTopBar
import com.hussein.jana.core.designsystem.JanaErrorState
import com.hussein.jana.core.designsystem.JanaLoadingState
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.btn_back
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.error_generic
import com.hussein.jana.core.designsystem.profile_edit_profile
import com.hussein.jana.core.designsystem.profile_photo_coming_soon
import com.hussein.jana.core.designsystem.profile_saved
import com.hussein.jana.feature.home.tabs.profile.presentation.edit.components.EditProfileForm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** How long the "Profile updated" snackbar is visible before the screen closes. */
private const val SAVED_FEEDBACK_DELAY_MS = 900L

// ── Entry (stateful) ──────────────────────────────────────────────────────────

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val savedText = stringResource(Res.string.profile_saved)
    val errorText = stringResource(Res.string.error_generic)
    val photoComingSoonText = stringResource(Res.string.profile_photo_coming_soon)

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EditProfileEffect.Saved -> {
                    launch { snackbarHostState.showSnackbar(savedText) }
                    delay(SAVED_FEEDBACK_DELAY_MS)
                    onBack()
                }
                EditProfileEffect.SaveFailed -> snackbarHostState.showSnackbar(errorText)
                EditProfileEffect.ShowPhotoComingSoon -> snackbarHostState.showSnackbar(photoComingSoonText)
                EditProfileEffect.NavigateBack -> onBack()
            }
        }
    }

    EditProfileContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
fun EditProfileContent(
    state: EditProfileUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (EditProfileIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            JanaBackTopBar(
                title = stringResource(Res.string.profile_edit_profile),
                onBack = { onIntent(EditProfileIntent.BackClicked) },
                backContentDescription = stringResource(Res.string.btn_back),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                JanaLoadingState()
            }
            state.loadError -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                JanaErrorState(
                    message = stringResource(Res.string.error_generic),
                    retryLabel = stringResource(Res.string.btn_retry),
                    onRetry = { onIntent(EditProfileIntent.Load) },
                )
            }
            else -> EditProfileForm(
                fullName = state.fullName,
                email = state.email,
                phone = state.phone,
                photoUrl = state.photoUrl,
                nameError = state.nameError,
                emailError = state.emailError,
                phoneError = state.phoneError,
                isSaving = state.isSaving,
                onNameChange = { onIntent(EditProfileIntent.NameChanged(it)) },
                onEmailChange = { onIntent(EditProfileIntent.EmailChanged(it)) },
                onPhoneChange = { onIntent(EditProfileIntent.PhoneChanged(it)) },
                onChangePhotoClick = { onIntent(EditProfileIntent.ChangePhotoClicked) },
                onSaveClick = { onIntent(EditProfileIntent.SaveClicked) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            )
        }
    }
}

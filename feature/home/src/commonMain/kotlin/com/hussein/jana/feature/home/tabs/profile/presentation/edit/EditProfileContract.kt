package com.hussein.jana.feature.home.tabs.profile.presentation.edit

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.profile.domain.model.ProfileFieldError

/** Field errors are typed; the screen maps them to localized messages. */
data class EditProfileUiState(
    val isLoading: Boolean = true,
    val loadError: Boolean = false,
    val isSaving: Boolean = false,
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String? = null,
    val nameError: ProfileFieldError? = null,
    val emailError: ProfileFieldError? = null,
    val phoneError: ProfileFieldError? = null,
) : MviState

sealed interface EditProfileIntent : MviIntent {
    data object Load : EditProfileIntent
    data class NameChanged(val value: String) : EditProfileIntent
    data class EmailChanged(val value: String) : EditProfileIntent
    data class PhoneChanged(val value: String) : EditProfileIntent
    data object SaveClicked : EditProfileIntent
    data object BackClicked : EditProfileIntent
}

sealed interface EditProfileEffect : MviEffect {
    data object Saved : EditProfileEffect
    data object SaveFailed : EditProfileEffect
    data object NavigateBack : EditProfileEffect
}

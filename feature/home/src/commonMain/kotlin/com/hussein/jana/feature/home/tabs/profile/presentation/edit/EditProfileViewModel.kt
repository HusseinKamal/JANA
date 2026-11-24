package com.hussein.jana.feature.home.tabs.profile.presentation.edit

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.profile.domain.model.StudentProfileValidator
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.GetStudentProfileUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.UpdateStudentProfileUseCase
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val getStudentProfile: GetStudentProfileUseCase,
    private val updateStudentProfile: UpdateStudentProfileUseCase,
) : BaseMviViewModel<EditProfileUiState, EditProfileIntent, EditProfileEffect>(
    EditProfileUiState(),
) {

    init {
        onIntent(EditProfileIntent.Load)
    }

    override fun onIntent(intent: EditProfileIntent) = when (intent) {
        EditProfileIntent.Load -> load()
        is EditProfileIntent.NameChanged -> setState { copy(fullName = intent.value, nameError = null) }
        is EditProfileIntent.EmailChanged -> setState { copy(email = intent.value, emailError = null) }
        is EditProfileIntent.PhoneChanged -> setState { copy(phone = intent.value, phoneError = null) }
        EditProfileIntent.SaveClicked -> save()
        EditProfileIntent.BackClicked -> sendEffect(EditProfileEffect.NavigateBack)
    }

    private fun load() {
        setState { copy(isLoading = true, loadError = false) }
        viewModelScope.launch {
            getStudentProfile()
                .onSuccess { profile ->
                    setState {
                        copy(
                            isLoading = false,
                            fullName = profile.fullName,
                            email = profile.email,
                            phone = profile.phone,
                            photoUrl = profile.photoUrl,
                        )
                    }
                }
                .onFailure { setState { copy(isLoading = false, loadError = true) } }
        }
    }

    private fun save() {
        val state = currentState()
        if (state.isSaving || state.isLoading) return

        val nameError = StudentProfileValidator.validateName(state.fullName)
        val emailError = StudentProfileValidator.validateEmail(state.email)
        val phoneError = StudentProfileValidator.validatePhone(state.phone)
        if (nameError != null || emailError != null || phoneError != null) {
            setState { copy(nameError = nameError, emailError = emailError, phoneError = phoneError) }
            return
        }

        setState { copy(isSaving = true) }
        viewModelScope.launch {
            updateStudentProfile(state.fullName, state.email, state.phone)
                // isSaving stays true: the screen closes after the saved feedback, and this
                // blocks a second save in the meantime.
                .onSuccess { sendEffect(EditProfileEffect.Saved) }
                .onFailure {
                    setState { copy(isSaving = false) }
                    sendEffect(EditProfileEffect.SaveFailed)
                }
        }
    }
}

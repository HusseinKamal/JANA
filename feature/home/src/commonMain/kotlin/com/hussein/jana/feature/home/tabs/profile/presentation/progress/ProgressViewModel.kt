package com.hussein.jana.feature.home.tabs.profile.presentation.progress

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.GetLearningProgressUseCase
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val getLearningProgress: GetLearningProgressUseCase,
) : BaseMviViewModel<ProgressUiState, ProgressIntent, ProgressEffect>(ProgressUiState()) {

    init {
        onIntent(ProgressIntent.Load)
    }

    override fun onIntent(intent: ProgressIntent) = when (intent) {
        ProgressIntent.Load, ProgressIntent.Retry -> load()
        is ProgressIntent.CourseClicked -> sendEffect(ProgressEffect.NavigateToCourseDetail(intent.courseId))
    }

    private fun load() {
        setState { copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            getLearningProgress()
                .onSuccess { setState { copy(isLoading = false, progress = it) } }
                .onFailure { setState { copy(isLoading = false, isError = true) } }
        }
    }
}

package com.hussein.jana.feature.home.tabs.profile.presentation.syllabus

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.GetSyllabusUseCase
import kotlinx.coroutines.launch

class SyllabusViewModel(
    private val getSyllabus: GetSyllabusUseCase,
) : BaseMviViewModel<SyllabusUiState, SyllabusIntent, SyllabusEffect>(SyllabusUiState()) {

    init {
        onIntent(SyllabusIntent.Load)
    }

    override fun onIntent(intent: SyllabusIntent) = when (intent) {
        SyllabusIntent.Load, SyllabusIntent.Retry -> load()
        is SyllabusIntent.CourseClicked -> sendEffect(SyllabusEffect.NavigateToCourseDetail(intent.courseId))
    }

    private fun load() {
        setState { copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            getSyllabus()
                .onSuccess { setState { copy(isLoading = false, courses = it) } }
                .onFailure { setState { copy(isLoading = false, isError = true) } }
        }
    }
}

package com.hussein.jana.feature.home.tabs.profile.presentation.progress

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.profile.domain.model.LearningProgress

data class ProgressUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val progress: LearningProgress? = null,
) : MviState

sealed interface ProgressIntent : MviIntent {
    data object Load : ProgressIntent
    data object Retry : ProgressIntent
    data class CourseClicked(val courseId: String) : ProgressIntent
}

sealed interface ProgressEffect : MviEffect {
    data class NavigateToCourseDetail(val courseId: String) : ProgressEffect
}

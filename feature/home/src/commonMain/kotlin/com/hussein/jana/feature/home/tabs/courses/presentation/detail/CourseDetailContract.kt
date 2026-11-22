package com.hussein.jana.feature.home.tabs.courses.presentation.detail

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseLesson
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseUnit

data class CourseDetailUiState(
    val isLoading: Boolean = true,
    val course: Course? = null,
    val units: List<CourseUnit> = emptyList(),
    val error: String? = null,
) : MviState

sealed interface CourseDetailIntent : MviIntent {
    data object Load : CourseDetailIntent
    data class OpenLesson(val lesson: CourseLesson) : CourseDetailIntent
    data object Back : CourseDetailIntent
}

sealed interface CourseDetailEffect : MviEffect {
    data class ShowMessage(val message: String) : CourseDetailEffect
    data object NavigateBack : CourseDetailEffect
}

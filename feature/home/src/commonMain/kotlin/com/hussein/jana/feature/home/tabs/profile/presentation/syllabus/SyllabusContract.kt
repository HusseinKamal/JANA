package com.hussein.jana.feature.home.tabs.profile.presentation.syllabus

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.profile.domain.model.SyllabusCourse

data class SyllabusUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val courses: List<SyllabusCourse> = emptyList(),
) : MviState

sealed interface SyllabusIntent : MviIntent {
    data object Load : SyllabusIntent
    data object Retry : SyllabusIntent
    data class CourseClicked(val courseId: String) : SyllabusIntent
}

sealed interface SyllabusEffect : MviEffect {
    data class NavigateToCourseDetail(val courseId: String) : SyllabusEffect
}

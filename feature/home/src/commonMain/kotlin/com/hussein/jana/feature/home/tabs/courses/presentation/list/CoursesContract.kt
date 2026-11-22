package com.hussein.jana.feature.home.tabs.courses.presentation.list

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.courses.domain.model.Course

data class CoursesUiState(
    val isLoading: Boolean = true,
    val courses: List<Course> = emptyList(),
    val error: String? = null,
) : MviState

sealed interface CoursesIntent : MviIntent {
    data object Load : CoursesIntent
    data class OpenCourse(val courseId: String) : CoursesIntent
    data class SubscribeClicked(val courseId: String) : CoursesIntent
}

sealed interface CoursesEffect : MviEffect {
    data class NavigateToCourseDetail(
        val courseId: String,
        val openCheckout: Boolean = false,
    ) : CoursesEffect
}

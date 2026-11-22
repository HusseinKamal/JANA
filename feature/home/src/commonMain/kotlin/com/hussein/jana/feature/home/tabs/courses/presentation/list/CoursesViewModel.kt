package com.hussein.jana.feature.home.tabs.courses.presentation.list

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCoursesUseCase
import kotlinx.coroutines.launch

class CoursesViewModel(
    private val getCourses: GetCoursesUseCase,
) : BaseMviViewModel<CoursesUiState, CoursesIntent, CoursesEffect>(CoursesUiState()) {

    init {
        onIntent(CoursesIntent.Load)
    }

    override fun onIntent(intent: CoursesIntent) = when (intent) {
        CoursesIntent.Load -> loadCourses()
        is CoursesIntent.OpenCourse ->
            sendEffect(CoursesEffect.NavigateToCourseDetail(intent.courseId))
    }

    private fun loadCourses() {
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = getCourses()
            if (result.isSuccess) {
                setState { copy(isLoading = false, courses = result.getOrDefault(emptyList())) }
            } else {
                setState {
                    copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Failed to load courses")
                }
            }
        }
    }
}

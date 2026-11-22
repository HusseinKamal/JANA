package com.hussein.jana.feature.home.tabs.courses.presentation.list

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCoursesUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.ObserveCoursesUseCase
import kotlinx.coroutines.launch

class CoursesViewModel(
    private val getCourses: GetCoursesUseCase,
    private val observeCourses: ObserveCoursesUseCase,
) : BaseMviViewModel<CoursesUiState, CoursesIntent, CoursesEffect>(CoursesUiState()) {

    init {
        onIntent(CoursesIntent.Load)
        // Reactive refresh: a backend-verified subscription updates the shared catalogue,
        // and the list picks it up here without reloading. Updates are applied only after
        // the first load so the initial spinner is not skipped.
        viewModelScope.launch {
            observeCourses().collect { list ->
                if (!currentState().isLoading) setState { copy(courses = list) }
            }
        }
    }

    override fun onIntent(intent: CoursesIntent) = when (intent) {
        CoursesIntent.Load -> loadCourses()
        is CoursesIntent.OpenCourse ->
            sendEffect(CoursesEffect.NavigateToCourseDetail(intent.courseId))
        // Checkout lives in Course Details only; the list just navigates there with the sheet open.
        is CoursesIntent.SubscribeClicked ->
            sendEffect(CoursesEffect.NavigateToCourseDetail(intent.courseId, openCheckout = true))
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

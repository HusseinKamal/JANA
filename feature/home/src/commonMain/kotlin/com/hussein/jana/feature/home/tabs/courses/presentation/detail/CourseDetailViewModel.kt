package com.hussein.jana.feature.home.tabs.courses.presentation.detail

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseDetailUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseUnitsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    private val courseId: String,
    private val getCourseDetail: GetCourseDetailUseCase,
    private val getCourseUnits: GetCourseUnitsUseCase,
) : BaseMviViewModel<CourseDetailUiState, CourseDetailIntent, CourseDetailEffect>(
    CourseDetailUiState(),
) {

    init {
        onIntent(CourseDetailIntent.Load)
    }

    override fun onIntent(intent: CourseDetailIntent) = when (intent) {
        CourseDetailIntent.Load -> loadDetail()
        is CourseDetailIntent.OpenLesson ->
            sendEffect(CourseDetailEffect.ShowMessage("lesson_coming_soon"))
        CourseDetailIntent.Back ->
            sendEffect(CourseDetailEffect.NavigateBack)
    }

    private fun loadDetail() {
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val detailDeferred = async { getCourseDetail(courseId) }
            val unitsDeferred = async { getCourseUnits(courseId) }

            val detailResult = detailDeferred.await()
            val unitsResult = unitsDeferred.await()

            if (detailResult.isSuccess) {
                setState {
                    copy(
                        isLoading = false,
                        course = detailResult.getOrNull(),
                        units = unitsResult.getOrDefault(emptyList()),
                        error = null,
                    )
                }
            } else {
                setState {
                    copy(
                        isLoading = false,
                        error = detailResult.exceptionOrNull()?.message ?: "Failed to load course",
                    )
                }
            }
        }
    }
}

package com.hussein.jana.feature.home.tabs.live.presentation.list

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson

data class LiveLessonsUiState(
    val isLoading: Boolean = true,
    val upcomingLessons: List<LiveLesson> = emptyList(),
    val nextLessons: List<LiveLesson> = emptyList(),
    val isCalendarSynced: Boolean = false,
    val error: String? = null,
) : MviState

sealed interface LiveLessonsIntent : MviIntent {
    data object LoadLessons : LiveLessonsIntent
    data object Refresh : LiveLessonsIntent
    data class JoinZoom(val url: String) : LiveLessonsIntent
    data class OpenLessonDetail(val lessonId: String) : LiveLessonsIntent
    data object SyncCalendarClicked : LiveLessonsIntent
}

sealed interface LiveLessonsEffect : MviEffect {
    data class OpenZoomUrl(val url: String) : LiveLessonsEffect
    data class OpenLessonDetail(val lessonId: String) : LiveLessonsEffect
    data class ShowSnackbar(val message: String) : LiveLessonsEffect
    data class SyncCalendarSuccess(val count: Int) : LiveLessonsEffect
}

package com.hussein.jana.feature.home.tabs.live.presentation.list

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson

/** Inner tab selection for the Live Lessons screen. */
enum class LiveTab { UPCOMING, NEXT, PREVIOUS }

data class LiveLessonsUiState(
    val selectedTab: LiveTab = LiveTab.UPCOMING,
    val isLoading: Boolean = true,
    /** Hero lesson: first LIVE_NOW, else first UPCOMING by date. */
    val upcomingLesson: LiveLesson? = null,
    /** Remaining future lessons after the hero. */
    val nextLessons: List<LiveLesson> = emptyList(),
    /** Past lessons (status = ENDED). */
    val previousLessons: List<LiveLesson> = emptyList(),
    val isCalendarSynced: Boolean = false,
    val error: String? = null,
) : MviState

sealed interface LiveLessonsIntent : MviIntent {
    data class TabSelected(val tab: LiveTab) : LiveLessonsIntent
    data object LoadLessons : LiveLessonsIntent
    data class JoinZoom(val lessonId: String, val url: String) : LiveLessonsIntent
    data class OpenLessonDetail(val lessonId: String) : LiveLessonsIntent
    data object SyncCalendarClicked : LiveLessonsIntent
}

sealed interface LiveLessonsEffect : MviEffect {
    data class OpenZoomUrl(val url: String) : LiveLessonsEffect
    data class NavigateToDetail(val lessonId: String) : LiveLessonsEffect
    /** Generic snackbar message; prefer the typed variants below when possible. */
    data class ShowSnackbar(val message: String) : LiveLessonsEffect
    data class SyncCalendarSuccess(val count: Int) : LiveLessonsEffect
    /** Emitted when JoinZoom is called but no URL is set on the lesson. */
    data object ShowNoZoomLink : LiveLessonsEffect
    /** Emitted when the calendar sync operation fails. */
    data object ShowCalendarSyncFailed : LiveLessonsEffect
}

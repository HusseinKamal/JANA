package com.hussein.jana.feature.home.tabs.live.presentation.list

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonStatus
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetNextLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetPreviousLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetUpcomingLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.SyncCalendarUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LiveLessonsViewModel(
    private val getUpcomingLiveLessons: GetUpcomingLiveLessonsUseCase,
    private val getNextLiveLessons: GetNextLiveLessonsUseCase,
    private val getPreviousLiveLessons: GetPreviousLiveLessonsUseCase,
    private val syncCalendar: SyncCalendarUseCase,
) : BaseMviViewModel<LiveLessonsUiState, LiveLessonsIntent, LiveLessonsEffect>(
    LiveLessonsUiState(),
) {

    init {
        loadLessons()
    }

    override fun onIntent(intent: LiveLessonsIntent) = when (intent) {
        is LiveLessonsIntent.TabSelected -> setState { copy(selectedTab = intent.tab) }
        LiveLessonsIntent.LoadLessons -> loadLessons()
        is LiveLessonsIntent.JoinZoom -> {
            if (intent.url.isNotBlank()) {
                sendEffect(LiveLessonsEffect.OpenZoomUrl(intent.url))
            } else {
                // No hardcoded string — the screen resolves live_no_zoom_link from string resources
                sendEffect(LiveLessonsEffect.ShowNoZoomLink)
            }
        }
        is LiveLessonsIntent.OpenLessonDetail ->
            sendEffect(LiveLessonsEffect.NavigateToDetail(intent.lessonId))
        LiveLessonsIntent.SyncCalendarClicked -> handleSyncCalendar()
    }

    private fun loadLessons() {
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val upcomingDeferred = async { getUpcomingLiveLessons() }
            val nextDeferred = async { getNextLiveLessons() }
            val previousDeferred = async { getPreviousLiveLessons() }

            val upcomingResult = upcomingDeferred.await()
            val nextResult = nextDeferred.await()
            val previousResult = previousDeferred.await()

            if (upcomingResult.isSuccess) {
                val upcomingAll = upcomingResult.getOrDefault(emptyList())
                // Hero: first LIVE_NOW lesson, else first UPCOMING by date
                val hero = upcomingAll.firstOrNull { it.status == LessonStatus.LIVE_NOW }
                    ?: upcomingAll.minByOrNull { it.startEpochMs }

                // Propagate any partial failures so the screen can display an error.
                // Finding 4 fix: don't silently clear error when next/previous loads fail.
                val partialError = when {
                    !nextResult.isSuccess ->
                        nextResult.exceptionOrNull()?.message ?: "Failed to load upcoming lessons"
                    !previousResult.isSuccess ->
                        previousResult.exceptionOrNull()?.message ?: "Failed to load previous lessons"
                    else -> null
                }

                setState {
                    copy(
                        isLoading = false,
                        upcomingLesson = hero,
                        nextLessons = nextResult.getOrDefault(emptyList()),
                        previousLessons = previousResult.getOrDefault(emptyList()),
                        error = partialError,
                    )
                }
            } else {
                val errorMsg = upcomingResult.exceptionOrNull()?.message
                    ?: "Failed to load live lessons"
                setState { copy(isLoading = false, error = errorMsg) }
            }
        }
    }

    private fun handleSyncCalendar() {
        val futureLessons = listOfNotNull(currentState().upcomingLesson) +
            currentState().nextLessons
        if (futureLessons.isEmpty()) return

        viewModelScope.launch {
            val result = syncCalendar(futureLessons)
            if (result.isSuccess && result.getOrDefault(false)) {
                setState { copy(isCalendarSynced = true) }
                sendEffect(LiveLessonsEffect.SyncCalendarSuccess(futureLessons.size))
            } else {
                // No hardcoded string — the screen resolves live_calendar_sync_failed
                sendEffect(LiveLessonsEffect.ShowCalendarSyncFailed)
            }
        }
    }
}

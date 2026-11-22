package com.hussein.jana.feature.home.tabs.live.presentation.list

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetNextLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetUpcomingLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.SyncCalendarUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LiveLessonsViewModel(
    private val getUpcomingLiveLessons: GetUpcomingLiveLessonsUseCase,
    private val getNextLiveLessons: GetNextLiveLessonsUseCase,
    private val syncCalendar: SyncCalendarUseCase,
) : BaseMviViewModel<LiveLessonsUiState, LiveLessonsIntent, LiveLessonsEffect>(
    LiveLessonsUiState()
) {

    init {
        loadLessons()
    }

    override fun onIntent(intent: LiveLessonsIntent) = when (intent) {
        LiveLessonsIntent.LoadLessons -> loadLessons()
        LiveLessonsIntent.Refresh -> loadLessons()
        is LiveLessonsIntent.JoinZoom -> sendEffect(LiveLessonsEffect.OpenZoomUrl(intent.url))
        is LiveLessonsIntent.OpenLessonDetail -> sendEffect(LiveLessonsEffect.OpenLessonDetail(intent.lessonId))
        LiveLessonsIntent.SyncCalendarClicked -> handleSyncCalendar()
    }

    fun loadLessons() {
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val upcomingDeferred = async { getUpcomingLiveLessons() }
            val nextDeferred = async { getNextLiveLessons() }
            val upcomingResult = upcomingDeferred.await()
            val nextResult = nextDeferred.await()

            if (upcomingResult.isSuccess && nextResult.isSuccess) {
                setState {
                    copy(
                        isLoading = false,
                        upcomingLessons = upcomingResult.getOrDefault(emptyList()),
                        nextLessons = nextResult.getOrDefault(emptyList()),
                        error = null,
                    )
                }
            } else {
                val errorMsg = upcomingResult.exceptionOrNull()?.message
                    ?: nextResult.exceptionOrNull()?.message
                    ?: "Failed to load live lessons"
                setState { copy(isLoading = false, error = errorMsg) }
            }
        }
    }

    private fun handleSyncCalendar() {
        val allLessons = currentState().upcomingLessons + currentState().nextLessons
        if (allLessons.isEmpty()) return

        viewModelScope.launch {
            val result = syncCalendar(allLessons)
            if (result.isSuccess && result.getOrDefault(false)) {
                setState { copy(isCalendarSynced = true) }
                sendEffect(LiveLessonsEffect.SyncCalendarSuccess(allLessons.size))
            } else {
                sendEffect(LiveLessonsEffect.ShowSnackbar("Calendar sync failed"))
            }
        }
    }
}

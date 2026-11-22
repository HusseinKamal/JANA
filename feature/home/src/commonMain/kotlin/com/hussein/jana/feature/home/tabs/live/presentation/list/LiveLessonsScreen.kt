package com.hussein.jana.feature.home.tabs.live.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaInnerTabRow
import com.hussein.jana.core.designsystem.JanaSectionHeader
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.live_calendar_sync_failed
import com.hussein.jana.core.designsystem.live_error_load_failed
import com.hussein.jana.core.designsystem.live_error_retry
import com.hussein.jana.core.designsystem.live_no_next
import com.hussein.jana.core.designsystem.live_no_previous
import com.hussein.jana.core.designsystem.live_no_upcoming
import com.hussein.jana.core.designsystem.live_next_sessions_title
import com.hussein.jana.core.designsystem.live_no_zoom_link
import com.hussein.jana.core.designsystem.live_previous_title
import com.hussein.jana.core.designsystem.live_sync_success
import com.hussein.jana.core.designsystem.live_tab_next
import com.hussein.jana.core.designsystem.live_tab_previous
import com.hussein.jana.core.designsystem.live_tab_upcoming
import com.hussein.jana.core.designsystem.live_upcoming_title
import com.hussein.jana.core.designsystem.openZoomUrl
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.HomeRoute
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson
import com.hussein.jana.feature.home.tabs.live.presentation.list.components.CalendarSyncCard
import com.hussein.jana.feature.home.tabs.live.presentation.list.components.NextLessonItem
import com.hussein.jana.feature.home.tabs.live.presentation.list.components.PreviousLessonItem
import com.hussein.jana.feature.home.tabs.live.presentation.list.components.UpcomingLessonCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// ── Entry (stateful) ─────────────────────────────────────────────────────────

@Composable
fun LiveLessonsScreen(
    viewModel: LiveLessonsViewModel = koinViewModel(),
    navigator: AppNavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Resolve all localised strings up-front so the LaunchedEffect is clean.
    val syncSuccessTemplate = stringResource(Res.string.live_sync_success)
    val noZoomLinkMsg = stringResource(Res.string.live_no_zoom_link)
    val calendarSyncFailedMsg = stringResource(Res.string.live_calendar_sync_failed)

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LiveLessonsEffect.OpenZoomUrl ->
                    openZoomUrl(effect.url)
                is LiveLessonsEffect.NavigateToDetail ->
                    navigator.navigate(HomeRoute.LessonDetail(effect.lessonId))
                is LiveLessonsEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message)
                is LiveLessonsEffect.SyncCalendarSuccess -> {
                    val msg = syncSuccessTemplate.replace("%1\$d", effect.count.toString())
                    snackbarHostState.showSnackbar(msg)
                }
                // Finding 1: typed effects resolved to string resources here, not in ViewModel
                LiveLessonsEffect.ShowNoZoomLink ->
                    snackbarHostState.showSnackbar(noZoomLinkMsg)
                LiveLessonsEffect.ShowCalendarSyncFailed ->
                    snackbarHostState.showSnackbar(calendarSyncFailedMsg)
            }
        }
    }

    LiveLessonsContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
fun LiveLessonsContent(
    state: LiveLessonsUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (LiveLessonsIntent) -> Unit,
) {
    val tabLabels = listOf(
        stringResource(Res.string.live_tab_upcoming),
        stringResource(Res.string.live_tab_next),
        stringResource(Res.string.live_tab_previous),
    )
    val selectedIndex = state.selectedTab.ordinal
    val retryLabel = stringResource(Res.string.btn_retry)
    val errorLoadFailed = stringResource(Res.string.live_error_load_failed)
    val errorRetry = stringResource(Res.string.live_error_retry)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            JanaInnerTabRow(
                tabs = tabLabels,
                selectedIndex = selectedIndex,
                onTabSelected = { idx ->
                    onIntent(LiveLessonsIntent.TabSelected(LiveTab.entries[idx]))
                },
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = ScholarlyTeal)
                    }
                }
                // Finding 3/4: full failure — all data empty + error set
                state.error != null &&
                    state.upcomingLesson == null &&
                    state.nextLessons.isEmpty() &&
                    state.previousLessons.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = errorRetry,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { onIntent(LiveLessonsIntent.LoadLessons) }) {
                                Text(text = retryLabel)
                            }
                        }
                    }
                }
                else -> {
                    when (state.selectedTab) {
                        LiveTab.UPCOMING -> UpcomingTab(
                            upcomingLesson = state.upcomingLesson,
                            isCalendarSynced = state.isCalendarSynced,
                            onIntent = onIntent,
                        )
                        LiveTab.NEXT -> NextTab(
                            lessons = state.nextLessons,
                            hasPartialError = state.error != null,
                            onIntent = onIntent,
                        )
                        LiveTab.PREVIOUS -> PreviousTab(
                            lessons = state.previousLessons,
                            hasPartialError = state.error != null,
                            onIntent = onIntent,
                        )
                    }
                }
            }
        }
    }
}

// ── Upcoming tab ──────────────────────────────────────────────────────────────

@Composable
private fun UpcomingTab(
    upcomingLesson: LiveLesson?,
    isCalendarSynced: Boolean,
    onIntent: (LiveLessonsIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            JanaSectionHeader(title = stringResource(Res.string.live_upcoming_title))
        }

        if (upcomingLesson != null) {
            item {
                UpcomingLessonCard(
                    lesson = upcomingLesson,
                    onLessonClick = {
                        onIntent(LiveLessonsIntent.OpenLessonDetail(upcomingLesson.id))
                    },
                    onJoinZoomClick = {
                        onIntent(
                            LiveLessonsIntent.JoinZoom(
                                lessonId = upcomingLesson.id,
                                url = upcomingLesson.zoomUrl,
                            )
                        )
                    },
                )
            }
        } else {
            item {
                Text(
                    text = stringResource(Res.string.live_no_upcoming),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            CalendarSyncCard(
                isSynced = isCalendarSynced,
                onSyncClick = { onIntent(LiveLessonsIntent.SyncCalendarClicked) },
            )
        }
    }
}

// ── Next tab ──────────────────────────────────────────────────────────────────

@Composable
private fun NextTab(
    lessons: List<LiveLesson>,
    hasPartialError: Boolean,
    onIntent: (LiveLessonsIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            JanaSectionHeader(title = stringResource(Res.string.live_next_sessions_title))
        }

        if (lessons.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Finding 4: distinguish empty-because-failed from genuinely empty
                val emptyText = if (hasPartialError) {
                    stringResource(Res.string.live_error_load_failed)
                } else {
                    stringResource(Res.string.live_no_next)
                }
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (hasPartialError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        } else {
            items(lessons, key = { it.id }) { lesson ->
                NextLessonItem(
                    lesson = lesson,
                    onClick = { onIntent(LiveLessonsIntent.OpenLessonDetail(lesson.id)) },
                )
            }
        }
    }
}

// ── Previous tab ──────────────────────────────────────────────────────────────

@Composable
private fun PreviousTab(
    lessons: List<LiveLesson>,
    hasPartialError: Boolean,
    onIntent: (LiveLessonsIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            JanaSectionHeader(title = stringResource(Res.string.live_previous_title))
        }

        if (lessons.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Finding 4: distinguish empty-because-failed from genuinely empty
                val emptyText = if (hasPartialError) {
                    stringResource(Res.string.live_error_load_failed)
                } else {
                    stringResource(Res.string.live_no_previous)
                }
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (hasPartialError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        } else {
            items(lessons, key = { it.id }) { lesson ->
                PreviousLessonItem(
                    lesson = lesson,
                    onClick = { onIntent(LiveLessonsIntent.OpenLessonDetail(lesson.id)) },
                )
            }
        }
    }
}

package com.hussein.jana.feature.home.tabs.live.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaSectionHeader
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.live_lessons_title
import com.hussein.jana.core.designsystem.live_next_title
import com.hussein.jana.core.designsystem.live_no_lessons
import com.hussein.jana.core.designsystem.live_no_upcoming
import com.hussein.jana.core.designsystem.live_sync_success
import com.hussein.jana.core.designsystem.live_upcoming_title
import com.hussein.jana.core.designsystem.openZoomUrl
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.HomeRoute
import com.hussein.jana.feature.home.tabs.live.presentation.list.components.CalendarSyncCard
import com.hussein.jana.feature.home.tabs.live.presentation.list.components.NextLessonItem
import com.hussein.jana.feature.home.tabs.live.presentation.list.components.UpcomingLessonCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LiveLessonsScreen(
    viewModel: LiveLessonsViewModel = koinViewModel(),
    navigator: AppNavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val syncSuccessTemplate = stringResource(Res.string.live_sync_success)

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LiveLessonsEffect.OpenZoomUrl -> {
                    openZoomUrl(effect.url)
                }
                is LiveLessonsEffect.OpenLessonDetail -> {
                    navigator.navigate(HomeRoute.LessonDetail(effect.lessonId))
                }
                is LiveLessonsEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is LiveLessonsEffect.SyncCalendarSuccess -> {
                    val msg = syncSuccessTemplate.replace("%1\$d", effect.count.toString())
                    snackbarHostState.showSnackbar(msg)
                }
            }
        }
    }

    LiveLessonsContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveLessonsContent(
    state: LiveLessonsUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (LiveLessonsIntent) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onIntent(LiveLessonsIntent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (state.isLoading && state.upcomingLessons.isEmpty() && state.nextLessons.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = ScholarlyTeal)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        Text(
                            text = stringResource(Res.string.live_lessons_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    item {
                        CalendarSyncCard(
                            isSynced = state.isCalendarSynced,
                            onSyncClick = { onIntent(LiveLessonsIntent.SyncCalendarClicked) },
                        )
                    }

                    item {
                        JanaSectionHeader(title = stringResource(Res.string.live_upcoming_title))
                    }

                    if (state.upcomingLessons.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(Res.string.live_no_upcoming),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        items(state.upcomingLessons, key = { it.id }) { lesson ->
                            UpcomingLessonCard(
                                lesson = lesson,
                                onLessonClick = { onIntent(LiveLessonsIntent.OpenLessonDetail(lesson.id)) },
                                onJoinZoomClick = { onIntent(LiveLessonsIntent.JoinZoom(lesson.zoomUrl)) },
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        JanaSectionHeader(title = stringResource(Res.string.live_next_title))
                    }

                    if (state.nextLessons.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(Res.string.live_no_lessons),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        items(state.nextLessons, key = { it.id }) { lesson ->
                            NextLessonItem(
                                lesson = lesson,
                                onClick = { onIntent(LiveLessonsIntent.OpenLessonDetail(lesson.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.hussein.jana.feature.home.tabs.profile.presentation.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaBackTopBar
import com.hussein.jana.core.designsystem.JanaEmptyState
import com.hussein.jana.core.designsystem.JanaErrorState
import com.hussein.jana.core.designsystem.JanaLabeledProgressBar
import com.hussein.jana.core.designsystem.JanaLoadingState
import com.hussein.jana.core.designsystem.JanaProgressRing
import com.hussein.jana.core.designsystem.JanaSectionHeader
import com.hussein.jana.core.designsystem.JanaStatCard
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.btn_back
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.error_generic
import com.hussein.jana.core.designsystem.profile_progress
import com.hussein.jana.core.designsystem.profile_progress_by_course
import com.hussein.jana.core.designsystem.profile_progress_courses
import com.hussein.jana.core.designsystem.profile_progress_empty
import com.hussein.jana.core.designsystem.profile_progress_exams
import com.hussein.jana.core.designsystem.profile_progress_live_attended
import com.hussein.jana.core.designsystem.profile_progress_overall
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.HomeRoute
import com.hussein.jana.feature.home.tabs.profile.domain.model.LearningProgress
import com.hussein.jana.feature.home.tabs.profile.presentation.common.lessonsProgressText
import com.hussein.jana.feature.home.tabs.profile.presentation.common.percentText
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// ── Entry (stateful) ──────────────────────────────────────────────────────────

@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    viewModel: ProgressViewModel = koinViewModel(),
    navigator: AppNavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProgressEffect.NavigateToCourseDetail ->
                    navigator.navigate(HomeRoute.CourseDetail(effect.courseId))
            }
        }
    }

    ProgressContent(state = state, onBack = onBack, onIntent = viewModel::onIntent)
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
fun ProgressContent(
    state: ProgressUiState,
    onBack: () -> Unit,
    onIntent: (ProgressIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            JanaBackTopBar(
                title = stringResource(Res.string.profile_progress),
                onBack = onBack,
                backContentDescription = stringResource(Res.string.btn_back),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        val centered = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        val progress = state.progress
        when {
            state.isLoading -> Box(centered, contentAlignment = Alignment.Center) { JanaLoadingState() }
            state.isError || progress == null -> Box(centered, contentAlignment = Alignment.Center) {
                JanaErrorState(
                    message = stringResource(Res.string.error_generic),
                    retryLabel = stringResource(Res.string.btn_retry),
                    onRetry = { onIntent(ProgressIntent.Retry) },
                )
            }
            progress.courses.isEmpty() -> Box(centered, contentAlignment = Alignment.Center) {
                JanaEmptyState(message = stringResource(Res.string.profile_progress_empty))
            }
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item(key = "overall") { OverallProgress(progress) }
                item(key = "stats") { ProgressStats(progress) }
                item(key = "by_course_header") {
                    JanaSectionHeader(title = stringResource(Res.string.profile_progress_by_course))
                }
                items(progress.courses, key = { it.courseId }) { course ->
                    JanaLabeledProgressBar(
                        title = course.title,
                        valueText = lessonsProgressText(course.completedLessons, course.totalLessons),
                        progress = course.fraction,
                        onClick = { onIntent(ProgressIntent.CourseClicked(course.courseId)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun OverallProgress(progress: LearningProgress) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        JanaProgressRing(
            progress = progress.overallFraction,
            size = 120.dp,
            strokeWidth = 10.dp,
            label = progress.overallFraction.percentText(),
        )
        Text(
            text = stringResource(Res.string.profile_progress_overall),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = lessonsProgressText(progress.completedLessons, progress.totalLessons),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Subscribed courses, attended live lessons (hidden when unknown) and completed lesson exams. */
@Composable
private fun ProgressStats(progress: LearningProgress) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val cardModifier = Modifier
            .weight(1f)
            .fillMaxHeight()
        JanaStatCard(
            value = progress.courses.size.toString(),
            label = stringResource(Res.string.profile_progress_courses),
            icon = Icons.AutoMirrored.Rounded.MenuBook,
            modifier = cardModifier,
        )
        progress.attendedLiveLessons?.let { attended ->
            JanaStatCard(
                value = attended.toString(),
                label = stringResource(Res.string.profile_progress_live_attended),
                icon = Icons.Rounded.Videocam,
                modifier = cardModifier,
            )
        }
        JanaStatCard(
            value = progress.examsCompleted.toString(),
            label = stringResource(Res.string.profile_progress_exams),
            icon = Icons.Rounded.Quiz,
            modifier = cardModifier,
        )
    }
}

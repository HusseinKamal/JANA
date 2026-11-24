package com.hussein.jana.feature.home.tabs.profile.presentation.syllabus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaBackTopBar
import com.hussein.jana.core.designsystem.JanaEmptyState
import com.hussein.jana.core.designsystem.JanaErrorState
import com.hussein.jana.core.designsystem.JanaLoadingState
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.btn_back
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.error_generic
import com.hussein.jana.core.designsystem.profile_syllabus
import com.hussein.jana.core.designsystem.profile_syllabus_empty
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.HomeRoute
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.components.CourseUnitSection
import com.hussein.jana.feature.home.tabs.profile.presentation.syllabus.components.SyllabusCourseHeader
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// ── Entry (stateful) ──────────────────────────────────────────────────────────

@Composable
fun SyllabusScreen(
    onBack: () -> Unit,
    viewModel: SyllabusViewModel = koinViewModel(),
    navigator: AppNavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SyllabusEffect.NavigateToCourseDetail ->
                    navigator.navigate(HomeRoute.CourseDetail(effect.courseId))
            }
        }
    }

    SyllabusContent(state = state, onBack = onBack, onIntent = viewModel::onIntent)
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
fun SyllabusContent(
    state: SyllabusUiState,
    onBack: () -> Unit,
    onIntent: (SyllabusIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            JanaBackTopBar(
                title = stringResource(Res.string.profile_syllabus),
                onBack = onBack,
                backContentDescription = stringResource(Res.string.btn_back),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        val centered = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when {
            state.isLoading -> Box(centered, contentAlignment = Alignment.Center) { JanaLoadingState() }
            state.isError -> Box(centered, contentAlignment = Alignment.Center) {
                JanaErrorState(
                    message = stringResource(Res.string.error_generic),
                    retryLabel = stringResource(Res.string.btn_retry),
                    onRetry = { onIntent(SyllabusIntent.Retry) },
                )
            }
            state.courses.isEmpty() -> Box(centered, contentAlignment = Alignment.Center) {
                JanaEmptyState(message = stringResource(Res.string.profile_syllabus_empty))
            }
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                state.courses.forEach { syllabusCourse ->
                    val course = syllabusCourse.course
                    item(key = "course_${course.id}") {
                        SyllabusCourseHeader(
                            title = course.title,
                            completedLessons = course.completedLessons,
                            totalLessons = course.totalLessons,
                            onClick = { onIntent(SyllabusIntent.CourseClicked(course.id)) },
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    items(syllabusCourse.units, key = { "unit_${course.id}_${it.id}" }) { unit ->
                        CourseUnitSection(
                            unit = unit,
                            onLessonClick = { onIntent(SyllabusIntent.CourseClicked(course.id)) },
                        )
                    }
                }
            }
        }
    }
}

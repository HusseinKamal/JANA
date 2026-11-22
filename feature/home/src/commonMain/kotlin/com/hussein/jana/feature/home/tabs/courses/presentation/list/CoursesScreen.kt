package com.hussein.jana.feature.home.tabs.courses.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.hussein.jana.core.designsystem.JanaSectionHeader
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.courses_no_courses
import com.hussein.jana.core.designsystem.courses_title
import com.hussein.jana.core.designsystem.error_generic
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.HomeRoute
import com.hussein.jana.feature.home.tabs.courses.presentation.list.components.JanaCourseCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// ── Entry (stateful) ──────────────────────────────────────────────────────────

@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = koinViewModel(),
    navigator: AppNavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CoursesEffect.NavigateToCourseDetail ->
                    navigator.navigate(HomeRoute.CourseDetail(effect.courseId))
            }
        }
    }

    CoursesContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
fun CoursesContent(
    state: CoursesUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (CoursesIntent) -> Unit,
) {
    // Finding 6: resolve courses_title so it is actually referenced in Kotlin
    val sectionTitle = stringResource(Res.string.courses_title)
    val retryLabel = stringResource(Res.string.btn_retry)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = ScholarlyTeal)
                }
            }
            // Finding 3: error branch before the empty-state check
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(Res.string.error_generic),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { onIntent(CoursesIntent.Load) }) {
                            Text(text = retryLabel)
                        }
                    }
                }
            }
            state.courses.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.courses_no_courses),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            else -> {
                // Finding 6: JanaSectionHeader uses the courses_title string resource
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    JanaSectionHeader(
                        title = sectionTitle,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.courses, key = { it.id }) { course ->
                            JanaCourseCard(
                                course = course,
                                onClick = { onIntent(CoursesIntent.OpenCourse(course.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

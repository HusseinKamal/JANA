package com.hussein.jana.feature.home.tabs.live.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.hussein.jana.core.designsystem.lesson_detail_title
import com.hussein.jana.core.designsystem.lesson_exam_coming_soon
import com.hussein.jana.core.designsystem.lesson_exams_title
import com.hussein.jana.core.designsystem.lesson_files_title
import com.hussein.jana.core.designsystem.lesson_no_exams
import com.hussein.jana.core.designsystem.lesson_no_files
import com.hussein.jana.core.designsystem.openUrl
import com.hussein.jana.core.designsystem.previewFile
import com.hussein.jana.feature.home.tabs.live.presentation.detail.components.LessonExamCard
import com.hussein.jana.feature.home.tabs.live.presentation.detail.components.LessonFileItem
import com.hussein.jana.feature.home.tabs.live.presentation.detail.components.LessonInfoHeader
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LessonDetailScreen(
    lessonId: String,
    onBack: () -> Unit,
    viewModel: LessonDetailViewModel = koinViewModel { parametersOf(lessonId) },
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val examComingSoon = stringResource(Res.string.lesson_exam_coming_soon)

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LessonDetailEffect.OpenFilePreview -> {
                    previewFile(effect.url, effect.mimeType)
                }
                is LessonDetailEffect.DownloadFile -> {
                    openUrl(effect.url)
                }
                is LessonDetailEffect.ShowExamSnackbar -> {
                    snackbarHostState.showSnackbar(examComingSoon)
                }
                is LessonDetailEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LessonDetailContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailContent(
    state: LessonDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onIntent: (LessonDetailIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.lesson_detail_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = ScholarlyTeal)
            }
        } else if (state.lesson != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    LessonInfoHeader(lesson = state.lesson)
                }

                item {
                    JanaSectionHeader(title = stringResource(Res.string.lesson_files_title))
                }

                if (state.files.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(Res.string.lesson_no_files),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    items(state.files, key = { it.id }) { file ->
                        LessonFileItem(
                            file = file,
                            onPreviewClick = { onIntent(LessonDetailIntent.PreviewFileClicked(file)) },
                            onDownloadClick = { onIntent(LessonDetailIntent.DownloadFileClicked(file)) },
                        )
                    }
                }

                item {
                    JanaSectionHeader(title = stringResource(Res.string.lesson_exams_title))
                }

                if (state.exams.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(Res.string.lesson_no_exams),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    items(state.exams, key = { it.id }) { exam ->
                        LessonExamCard(
                            exam = exam,
                            onStartExamClick = { onIntent(LessonDetailIntent.StartExamClicked(exam)) },
                        )
                    }
                }
            }
        }
    }
}

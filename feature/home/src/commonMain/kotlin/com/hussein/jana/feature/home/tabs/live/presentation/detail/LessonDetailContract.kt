package com.hussein.jana.feature.home.tabs.live.presentation.detail

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonExam
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFile
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson

data class LessonDetailUiState(
    val isLoading: Boolean = true,
    val lesson: LiveLesson? = null,
    val files: List<LessonFile> = emptyList(),
    val exams: List<LessonExam> = emptyList(),
    val error: String? = null,
) : MviState

sealed interface LessonDetailIntent : MviIntent {
    data class LoadDetail(val lessonId: String) : LessonDetailIntent
    data class PreviewFileClicked(val file: LessonFile) : LessonDetailIntent
    data class DownloadFileClicked(val file: LessonFile) : LessonDetailIntent
    data class StartExamClicked(val exam: LessonExam) : LessonDetailIntent
}

sealed interface LessonDetailEffect : MviEffect {
    data class OpenFilePreview(val url: String, val mimeType: String) : LessonDetailEffect
    data class DownloadFile(val url: String) : LessonDetailEffect
    data class ShowExamSnackbar(val message: String) : LessonDetailEffect
    data class ShowError(val message: String) : LessonDetailEffect
}

package com.hussein.jana.feature.home.tabs.live.presentation.detail

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFile
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFileType
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetLessonDetailsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetLessonExamsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetLessonFilesUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LessonDetailViewModel(
    private val lessonId: String,
    private val getLessonDetails: GetLessonDetailsUseCase,
    private val getLessonFiles: GetLessonFilesUseCase,
    private val getLessonExams: GetLessonExamsUseCase,
) : BaseMviViewModel<LessonDetailUiState, LessonDetailIntent, LessonDetailEffect>(
    LessonDetailUiState()
) {

    init {
        loadDetail()
    }

    override fun onIntent(intent: LessonDetailIntent) = when (intent) {
        is LessonDetailIntent.LoadDetail -> loadDetail()
        is LessonDetailIntent.PreviewFileClicked -> handlePreviewFile(intent.file)
        is LessonDetailIntent.DownloadFileClicked -> sendEffect(LessonDetailEffect.DownloadFile(intent.file.downloadUrl))
        is LessonDetailIntent.StartExamClicked -> sendEffect(
            LessonDetailEffect.ShowExamSnackbar(intent.exam.id)
        )
    }

    fun loadDetail() {
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val detailsDeferred = async { getLessonDetails(lessonId) }
            val filesDeferred = async { getLessonFiles(lessonId) }
            val examsDeferred = async { getLessonExams(lessonId) }

            val detailsResult = detailsDeferred.await()
            val filesResult = filesDeferred.await()
            val examsResult = examsDeferred.await()

            if (detailsResult.isSuccess) {
                setState {
                    copy(
                        isLoading = false,
                        lesson = detailsResult.getOrNull(),
                        files = filesResult.getOrDefault(emptyList()),
                        exams = examsResult.getOrDefault(emptyList()),
                        error = null,
                    )
                }
            } else {
                val errorMsg = detailsResult.exceptionOrNull()?.message ?: "Failed to load lesson detail"
                setState { copy(isLoading = false, error = errorMsg) }
            }
        }
    }

    private fun handlePreviewFile(file: LessonFile) {
        val mimeType = when (file.fileType) {
            LessonFileType.PDF -> "application/pdf"
            LessonFileType.DOCX -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            LessonFileType.PPTX -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            LessonFileType.OTHER -> "application/octet-stream"
        }
        sendEffect(LessonDetailEffect.OpenFilePreview(file.downloadUrl, mimeType))
    }
}

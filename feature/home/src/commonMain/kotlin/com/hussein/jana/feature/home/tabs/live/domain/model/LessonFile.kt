package com.hussein.jana.feature.home.tabs.live.domain.model

data class LessonFile(
    val id: String,
    val title: String,
    val sizeText: String,
    val fileType: LessonFileType,
    val downloadUrl: String,
)

package com.hussein.jana.feature.home.tabs.live.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LessonFileDto(
    val id: String,
    val title: String,
    val sizeText: String,
    val fileType: String,
    val downloadUrl: String,
)

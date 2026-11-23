package com.hussein.jana.feature.home.tabs.teacher.domain.model

data class ChatMessage(
    val id: String,
    val text: String,
    val sentAtMs: Long,
    val isFromStudent: Boolean,
    val isRead: Boolean,
)

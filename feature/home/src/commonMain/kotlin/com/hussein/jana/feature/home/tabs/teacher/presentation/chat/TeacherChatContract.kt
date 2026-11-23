package com.hussein.jana.feature.home.tabs.teacher.presentation.chat

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.feature.home.tabs.teacher.domain.model.ChatMessage
import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherProfile

data class TeacherChatUiState(
    val isLoading: Boolean = true,
    val teacher: TeacherProfile? = null,
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    val error: String? = null,
) : MviState {
    val canSend: Boolean get() = input.isNotBlank() && !isSending
}

sealed interface TeacherChatIntent : MviIntent {
    data object Load : TeacherChatIntent
    data class InputChanged(val text: String) : TeacherChatIntent
    data object Send : TeacherChatIntent
    data object Back : TeacherChatIntent
}

sealed interface TeacherChatEffect : MviEffect {
    data object NavigateBack : TeacherChatEffect
    data object ShowSendFailed : TeacherChatEffect
}

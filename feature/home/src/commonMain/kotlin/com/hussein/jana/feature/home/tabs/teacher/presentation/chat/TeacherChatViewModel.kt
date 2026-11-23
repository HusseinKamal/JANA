package com.hussein.jana.feature.home.tabs.teacher.presentation.chat

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetActiveTeacherProfileUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetTeacherChatMessagesUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.SendTeacherMessageUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TeacherChatViewModel(
    private val getActiveTeacherProfile: GetActiveTeacherProfileUseCase,
    private val getTeacherChatMessages: GetTeacherChatMessagesUseCase,
    private val sendTeacherMessage: SendTeacherMessageUseCase,
) : BaseMviViewModel<TeacherChatUiState, TeacherChatIntent, TeacherChatEffect>(TeacherChatUiState()) {

    init {
        onIntent(TeacherChatIntent.Load)
    }

    override fun onIntent(intent: TeacherChatIntent) = when (intent) {
        TeacherChatIntent.Load -> load()
        is TeacherChatIntent.InputChanged -> setState { copy(input = intent.text) }
        TeacherChatIntent.Send -> send()
        TeacherChatIntent.Back -> sendEffect(TeacherChatEffect.NavigateBack)
    }

    private fun load() {
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val profileDeferred = async { getActiveTeacherProfile() }
            val messagesDeferred = async { getTeacherChatMessages() }
            val profile = profileDeferred.await()
            val messages = messagesDeferred.await()
            val failure = profile.exceptionOrNull() ?: messages.exceptionOrNull()
            if (failure != null) {
                setState {
                    copy(isLoading = false, error = failure.message ?: "Failed to load chat")
                }
            } else {
                setState {
                    copy(
                        isLoading = false,
                        teacher = profile.getOrNull(),
                        messages = messages.getOrDefault(emptyList()),
                    )
                }
            }
        }
    }

    private fun send() {
        val current = currentState()
        if (!current.canSend) return
        setState { copy(isSending = true) }
        viewModelScope.launch {
            sendTeacherMessage(current.input)
                .onSuccess { message ->
                    setState { copy(isSending = false, input = "", messages = messages + message) }
                }
                .onFailure {
                    setState { copy(isSending = false) }
                    sendEffect(TeacherChatEffect.ShowSendFailed)
                }
        }
    }
}

package com.hussein.jana.feature.home.tabs.teacher.domain.usecase

import com.hussein.jana.feature.home.tabs.teacher.domain.model.ChatMessage
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherChatRepository

class SendTeacherMessageUseCase(private val repository: TeacherChatRepository) {
    suspend operator fun invoke(text: String): Result<ChatMessage> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Message must not be blank"))
        }
        return repository.sendMessage(trimmed)
    }
}

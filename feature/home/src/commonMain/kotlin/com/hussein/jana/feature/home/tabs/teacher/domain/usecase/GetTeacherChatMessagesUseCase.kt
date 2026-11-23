package com.hussein.jana.feature.home.tabs.teacher.domain.usecase

import com.hussein.jana.feature.home.tabs.teacher.domain.model.ChatMessage
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherChatRepository

class GetTeacherChatMessagesUseCase(private val repository: TeacherChatRepository) {
    suspend operator fun invoke(): Result<List<ChatMessage>> = repository.getMessages()
}

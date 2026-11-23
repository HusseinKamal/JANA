package com.hussein.jana.feature.home.tabs.teacher.domain.repository

import com.hussein.jana.feature.home.tabs.teacher.domain.model.ChatMessage

/** Conversation between the student and the active teacher. */
interface TeacherChatRepository {
    suspend fun getMessages(): Result<List<ChatMessage>>
    suspend fun sendMessage(text: String): Result<ChatMessage>
}

package com.hussein.jana.feature.home.tabs.teacher.data.repository

import com.hussein.jana.feature.home.tabs.teacher.data.mock.MockTeacherData
import com.hussein.jana.feature.home.tabs.teacher.domain.model.ChatMessage
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherChatRepository
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// TODO: replace with real API / WebSocket — GET /api/v1/conversations,
//  GET /api/v1/conversations/{id}/messages, POST /api/v1/conversations/{id}/messages
@OptIn(ExperimentalTime::class)
class TeacherChatRepositoryImpl : TeacherChatRepository {

    private val messages: MutableList<ChatMessage> =
        MockTeacherData.seedMessages(nowMs()).toMutableList()
    private var counter = 0

    // TODO: replace with real API call — GET /api/v1/conversations/{id}/messages
    override suspend fun getMessages(): Result<List<ChatMessage>> {
        delay(300)
        return Result.success(messages.toList())
    }

    // TODO: replace with real API call — POST /api/v1/conversations/{id}/messages
    override suspend fun sendMessage(text: String): Result<ChatMessage> {
        delay(300)
        val sentAt = nowMs()
        val message = ChatMessage(
            id = "local_${sentAt}_${counter++}",
            text = text,
            sentAtMs = sentAt,
            isFromStudent = true,
            isRead = false,
        )
        messages += message
        return Result.success(message)
    }

    private fun nowMs(): Long = Clock.System.now().toEpochMilliseconds()
}

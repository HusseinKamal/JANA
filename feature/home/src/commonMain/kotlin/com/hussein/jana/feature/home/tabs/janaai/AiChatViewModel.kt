package com.hussein.jana.feature.home.tabs.janaai

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel for the JANA AI Learning Chat screen.
 *
 * Uses a simple instance-level counter to generate KMP-safe unique message IDs
 * (avoids System.currentTimeMillis() which is JVM-only).
 *
 * The 1 200 ms AI-reply delay is driven by the [viewModelScope] dispatcher which,
 * in tests, is controlled by [kotlinx.coroutines.test.StandardTestDispatcher] via
 * [kotlinx.coroutines.Dispatchers.setMain], making the delay fully testable with
 * virtual time.
 */
class AiChatViewModel : BaseMviViewModel<AiChatState, AiChatIntent, AiChatEffect>(AiChatState()) {

    /** Counter used to generate unique message IDs without JVM-only APIs. */
    private var msgIdCounter = 0

    private fun nextId(): String = "msg_${++msgIdCounter}"

    override fun onIntent(intent: AiChatIntent) = when (intent) {
        is AiChatIntent.InputChanged -> setState { copy(inputText = intent.text) }
        AiChatIntent.SendMessage -> sendUserMessage()
        AiChatIntent.MicPressed -> sendEffect(
            AiChatEffect.ShowSnackbar(AiSnackbarMessage.VoiceComingSoon),
        )
        is AiChatIntent.QuickReplySelected -> sendUserReply(intent.replyId)
        is AiChatIntent.QuickActionClicked -> sendEffect(
            AiChatEffect.ShowSnackbar(AiSnackbarMessage.ActionComingSoon),
        )
        AiChatIntent.MenuClicked -> sendEffect(
            AiChatEffect.ShowSnackbar(AiSnackbarMessage.HistoryComingSoon),
        )
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun sendUserMessage() {
        val text = currentState().inputText.trim()
        if (text.isBlank()) return

        val userMsg = AiMessage(
            id = nextId(),
            isFromUser = true,
            content = text,
            timestamp = "Just now",
        )
        setState { copy(messages = messages + userMsg, inputText = "", isTyping = true) }

        viewModelScope.launch {
            delay(1_200L)
            val aiReply = AiMessage(
                id = nextId(),
                isFromUser = false,
                content = "Great thinking! Let's continue working through this step by step.",
                timestamp = "Just now",
                badge = "Socratic Guide",
            )
            setState { copy(messages = messages + aiReply, isTyping = false) }
            sendEffect(AiChatEffect.ScrollToBottom)
        }
    }

    private fun sendUserReply(replyId: String) {
        val reply = currentState().quickReplies.firstOrNull { it.id == replyId } ?: return
        setState { copy(inputText = reply.label) }
        sendUserMessage()
    }
}

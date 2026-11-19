package com.hussein.jana.feature.home.tabs.janaai

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

// ── Data models ───────────────────────────────────────────────────────────────

/**
 * A single chat message — either from the user or from JANA AI.
 *
 * AI messages support rich layout with optional sections:
 *  - [highlightPhrase]   amber pill above the main content
 *  - [equationDisplay]   monospace centered equation surface
 *  - [content2]          text shown after the equation
 *  - [nextStepPrompt]    tinted "YOUR NEXT STEP" box at the bottom
 */
data class AiMessage(
    val id: String,
    val isFromUser: Boolean,
    val content: String,
    val timestamp: String,
    val badge: String? = null,
    /** Bolded, coloured header shown above content in an amber pill. */
    val highlightPhrase: String? = null,
    /** Bold centred text displayed inside a surface between content and content2. */
    val equationDisplay: String? = null,
    /** Second content paragraph shown after the equation. */
    val content2: String? = null,
    /** Tinted prompt box shown at the bottom of an AI message. */
    val nextStepPrompt: String? = null,
)

/** A suggested quick-reply chip. */
data class QuickReply(val id: String, val label: String)

/**
 * A quick-action card in the session-looming banner.
 *
 * [titleKey] and [subtitleKey] are string resource keys resolved in the UI
 * layer via a `when` expression (no reflection).
 */
data class AiQuickAction(
    val id: String,
    val emoji: String,
    val titleKey: String,
    val subtitleKey: String,
)

// ── Default data ──────────────────────────────────────────────────────────────

fun defaultMessages(): List<AiMessage> = listOf(
    AiMessage(
        id = "1",
        isFromUser = false,
        content = "Welcome back Hussein! Yesterday in Dr. Ahmed's session, he covered the " +
            "quadratic formula. Let's do a fast Socratic check: In the equation:",
        equationDisplay = "2x² - 4x - 6 = 0",
        content2 = "What is your first strategic step before applying the formula?",
        timestamp = "11:04 AM",
        badge = "Socratic Primer",
    ),
    AiMessage(
        id = "2",
        isFromUser = true,
        content = "Can we divide the entire equation by 2 to simplify?",
        timestamp = "11:06 AM",
    ),
    AiMessage(
        id = "3",
        isFromUser = false,
        highlightPhrase = "Spot on, mathematical elegance!",
        content = "That simplifies it to x² - 2x - 3 = 0, which makes calculating the " +
            "discriminant b² - 4ac far less prone to arithmetic slip-ups.",
        nextStepPrompt = "What is the value of the discriminant now?",
        timestamp = "Just now",
        badge = "Reasoning Feedback",
    ),
)

fun defaultQuickReplies(): List<QuickReply> = listOf(
    QuickReply("r1", "b² - 4ac = 16"),
    QuickReply("r2", "b² - 4ac = 8"),
    QuickReply("r3", "Explain discriminant again"),
)

fun defaultQuickActions(): List<AiQuickAction> = listOf(
    AiQuickAction("a1", "📖", "ai_action_prep", "ai_action_prep_sub"),
    AiQuickAction("a2", "📋", "ai_action_review", "ai_action_review_sub"),
    AiQuickAction("a3", "🧠", "ai_action_socratic", "ai_action_socratic_sub"),
    AiQuickAction("a4", "⏱", "ai_action_exam", "ai_action_exam_sub"),
)

// ── MVI State ─────────────────────────────────────────────────────────────────

data class AiChatState(
    val userName: String = "Hussein",
    val messages: List<AiMessage> = defaultMessages(),
    val inputText: String = "",
    val quickReplies: List<QuickReply> = defaultQuickReplies(),
    val isTyping: Boolean = false,
    val sessionSubject: String = "Mathematics",
    val sessionTeacher: String = "Dr. Ahmed Hassan",
    val sessionTime: String = "18:00",
    val sessionFocus: String = "Quadratic Applications & Parabolic Modeling",
    val quickActions: List<AiQuickAction> = defaultQuickActions(),
    val statsText: String = "16 Sessions • 3 Active Subjects",
) : MviState

// ── MVI Intent ────────────────────────────────────────────────────────────────

sealed interface AiChatIntent : MviIntent {
    data class InputChanged(val text: String) : AiChatIntent
    data object SendMessage : AiChatIntent
    data object MicPressed : AiChatIntent
    data class QuickReplySelected(val replyId: String) : AiChatIntent
    data class QuickActionClicked(val actionId: String) : AiChatIntent
    data object MenuClicked : AiChatIntent
}

// ── MVI Effect ────────────────────────────────────────────────────────────────

sealed interface AiChatEffect : MviEffect {
    data class ShowSnackbar(val message: String) : AiChatEffect
    data object ScrollToBottom : AiChatEffect
}

package com.hussein.jana.feature.home.tabs.janaai.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hussein.jana.core.designsystem.JanaChatBubble
import com.hussein.jana.feature.home.tabs.janaai.AiMessage

/**
 * Right-aligned user message bubble.
 *
 * @param msg      The message to display.
 * @param userName The user's display name appended after the timestamp.
 */
@Composable
internal fun UserMessageBubble(
    msg: AiMessage,
    userName: String,
    modifier: Modifier = Modifier,
) {
    JanaChatBubble(
        text = msg.content,
        metaText = "${msg.timestamp} • $userName",
        isOutgoing = true,
        modifier = modifier,
    )
}

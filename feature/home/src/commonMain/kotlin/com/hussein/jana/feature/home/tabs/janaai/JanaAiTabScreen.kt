package com.hussein.jana.feature.home.tabs.janaai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaChatInput
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.VellumCanvas
import com.hussein.jana.core.designsystem.ai_action_coming_soon
import com.hussein.jana.core.designsystem.ai_choose_response
import com.hussein.jana.core.designsystem.ai_history_coming_soon
import com.hussein.jana.core.designsystem.ai_input_hint
import com.hussein.jana.core.designsystem.ai_voice_coming_soon
import com.hussein.jana.feature.home.tabs.janaai.components.AiChatHeader
import com.hussein.jana.feature.home.tabs.janaai.components.AiMessageBubble
import com.hussein.jana.feature.home.tabs.janaai.components.AiSessionBanner
import com.hussein.jana.feature.home.tabs.janaai.components.AiTypingIndicator
import com.hussein.jana.feature.home.tabs.janaai.components.QuickRepliesRow
import com.hussein.jana.feature.home.tabs.janaai.components.UserMessageBubble
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// ── Entry point (stateful) ────────────────────────────────────────────────────

/**
 * Stateful entry-point for the JANA AI Learning Chat tab.
 *
 * Owns the scroll state and snackbar host so effects from [AiChatViewModel]
 * can drive them without coupling the ViewModel to Compose.
 */
@Composable
fun JanaAiTabScreen() {
    val viewModel: AiChatViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()

    // Resolve localised snackbar strings outside the LaunchedEffect so the
    // composable's string resources are available to the suspend lambda.
    val msgVoice = stringResource(Res.string.ai_voice_coming_soon)
    val msgHistory = stringResource(Res.string.ai_history_coming_soon)
    val msgAction = stringResource(Res.string.ai_action_coming_soon)

    // Collect one-shot effects
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AiChatEffect.ShowSnackbar -> {
                    val message = when (effect.messageType) {
                        AiSnackbarMessage.VoiceComingSoon -> msgVoice
                        AiSnackbarMessage.HistoryComingSoon -> msgHistory
                        AiSnackbarMessage.ActionComingSoon -> msgAction
                    }
                    snackbarHostState.showSnackbar(message)
                }
                AiChatEffect.ScrollToBottom -> {
                    val count = lazyListState.layoutInfo.totalItemsCount
                    if (count > 0) lazyListState.animateScrollToItem(count - 1)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        JanaAiTabContent(
            state = state,
            onIntent = viewModel::onIntent,
            lazyListState = lazyListState,
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        )
    }
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
internal fun JanaAiTabContent(
    state: AiChatState,
    onIntent: (AiChatIntent) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VellumCanvas),
    ) {
        // 1. Sticky header
        AiChatHeader(
            state = state,
            onMenuClick = { onIntent(AiChatIntent.MenuClicked) },
        )

        // 2. Scrollable message list
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = lazyListState,
            contentPadding = PaddingValues(bottom = 8.dp),
        ) {
            // Session looming banner
            item {
                AiSessionBanner(
                    state = state,
                    onActionClick = { onIntent(AiChatIntent.QuickActionClicked(it)) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Chat messages
            items(state.messages) { msg ->
                if (msg.isFromUser) {
                    UserMessageBubble(msg = msg, userName = state.userName)
                } else {
                    AiMessageBubble(msg = msg)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Typing indicator while AI is composing
            if (state.isTyping) {
                item {
                    AiTypingIndicator()
                }
            }
        }

        // 3. Quick-reply suggestions
        QuickRepliesRow(
            replies = state.quickReplies,
            onReplyClick = { onIntent(AiChatIntent.QuickReplySelected(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )

        // 4. Divider
        HorizontalDivider(color = OutlineVariant)

        // 5. Response hint label
        Text(
            text = stringResource(Res.string.ai_choose_response),
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        // 6. Chat input
        JanaChatInput(
            text = state.inputText,
            onTextChange = { onIntent(AiChatIntent.InputChanged(it)) },
            onSendClick = { onIntent(AiChatIntent.SendMessage) },
            onMicClick = { onIntent(AiChatIntent.MicPressed) },
            hint = stringResource(Res.string.ai_input_hint),
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        // 7. Bottom inset spacer
        Spacer(modifier = Modifier.height(4.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

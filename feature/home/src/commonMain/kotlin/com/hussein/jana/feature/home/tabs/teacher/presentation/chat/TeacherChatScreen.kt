package com.hussein.jana.feature.home.tabs.teacher.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaAvatar
import com.hussein.jana.core.designsystem.JanaChatBubble
import com.hussein.jana.core.designsystem.JanaChatInput
import com.hussein.jana.core.designsystem.JanaEmptyState
import com.hussein.jana.core.designsystem.JanaErrorState
import com.hussein.jana.core.designsystem.JanaLoadingState
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.error_generic
import com.hussein.jana.core.designsystem.nav_back
import com.hussein.jana.core.designsystem.teacher_chat_empty
import com.hussein.jana.core.designsystem.teacher_chat_input_hint
import com.hussein.jana.core.designsystem.teacher_chat_send_failed
import com.hussein.jana.core.designsystem.teacher_chat_title
import com.hussein.jana.feature.home.common.teacherDisplayName
import com.hussein.jana.feature.home.tabs.teacher.presentation.common.formatTimeOfDay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherChatScreen(
    onBack: () -> Unit,
    viewModel: TeacherChatViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sendFailed = stringResource(Res.string.teacher_chat_send_failed)

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TeacherChatEffect.NavigateBack -> onBack()
                TeacherChatEffect.ShowSendFailed -> snackbarHostState.showSnackbar(sendFailed)
            }
        }
    }

    TeacherChatContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherChatContent(
    state: TeacherChatUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onIntent: (TeacherChatIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val teacher = state.teacher
                    if (teacher == null) {
                        Text(
                            text = stringResource(Res.string.teacher_chat_title),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            JanaAvatar(
                                name = teacher.name,
                                imageUrl = teacher.photoUrl,
                                size = 36.dp,
                            )
                            Text(
                                text = teacherDisplayName(teacher.name, teacher.gender),
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.nav_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                JanaChatInput(
                    text = state.input,
                    onTextChange = { onIntent(TeacherChatIntent.InputChanged(it)) },
                    onSendClick = { onIntent(TeacherChatIntent.Send) },
                    hint = stringResource(Res.string.teacher_chat_input_hint),
                    enabled = !state.isSending,
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val bodyModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when {
            state.isLoading -> Box(bodyModifier, contentAlignment = Alignment.Center) {
                JanaLoadingState()
            }
            state.error != null -> Box(bodyModifier, contentAlignment = Alignment.Center) {
                JanaErrorState(
                    message = stringResource(Res.string.error_generic),
                    retryLabel = stringResource(Res.string.btn_retry),
                    onRetry = { onIntent(TeacherChatIntent.Load) },
                )
            }
            state.messages.isEmpty() -> Box(bodyModifier, contentAlignment = Alignment.Center) {
                JanaEmptyState(message = stringResource(Res.string.teacher_chat_empty))
            }
            else -> LazyColumn(
                modifier = bodyModifier,
                // Newest message sits at the bottom, above the input bar.
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                items(state.messages.asReversed(), key = { it.id }) { message ->
                    JanaChatBubble(
                        text = message.text,
                        metaText = formatTimeOfDay(message.sentAtMs),
                        isOutgoing = message.isFromStudent,
                    )
                }
            }
        }
    }
}

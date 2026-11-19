package com.hussein.jana.feature.home.tabs.janaai.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.ai_action_exam
import com.hussein.jana.core.designsystem.ai_action_exam_sub
import com.hussein.jana.core.designsystem.ai_action_prep
import com.hussein.jana.core.designsystem.ai_action_prep_sub
import com.hussein.jana.core.designsystem.ai_action_review
import com.hussein.jana.core.designsystem.ai_action_review_sub
import com.hussein.jana.core.designsystem.ai_action_socratic
import com.hussein.jana.core.designsystem.ai_action_socratic_sub
import com.hussein.jana.feature.home.tabs.janaai.AiChatState
import com.hussein.jana.feature.home.tabs.janaai.AiQuickAction
import org.jetbrains.compose.resources.stringResource

/**
 * Teal session-looming card shown at the top of the chat list.
 *
 * Shows the upcoming session details and 2×2 quick-action grid.
 */
@Composable
internal fun AiSessionBanner(
    state: AiChatState,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ScholarlyTeal,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Session looming badge ─────────────────────────────────────────
            Row {
                Surface(
                    shape = CircleShape,
                    color = AmberGold.copy(alpha = 0.2f),
                ) {
                    Text(
                        text = "SESSION LOOMING • TODAY ${state.sessionTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Session greeting ──────────────────────────────────────────────
            Text(
                text = "Hi ${state.userName} 👋 You have a ${state.sessionSubject} session " +
                    "with ${state.sessionTeacher}.",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Focus: ${state.sessionFocus}. Let's prime your conceptual intuition beforehand.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── 2×2 quick-action grid ─────────────────────────────────────────
            val actions = state.quickActions
            if (actions.size >= 4) {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuickActionItem(
                            action = actions[0],
                            onActionClick = onActionClick,
                            modifier = Modifier.weight(1f),
                        )
                        QuickActionItem(
                            action = actions[1],
                            onActionClick = onActionClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuickActionItem(
                            action = actions[2],
                            onActionClick = onActionClick,
                            modifier = Modifier.weight(1f),
                        )
                        QuickActionItem(
                            action = actions[3],
                            onActionClick = onActionClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

// ── Quick action card ─────────────────────────────────────────────────────────

@Composable
private fun QuickActionItem(
    action: AiQuickAction,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.12f),
        onClick = { onActionClick(action.id) },
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = action.emoji,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = actionTitleString(action.titleKey),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = actionSubtitleString(action.subtitleKey),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}

// ── String resource resolvers (no reflection) ─────────────────────────────────

@Composable
private fun actionTitleString(key: String): String = when (key) {
    "ai_action_prep" -> stringResource(Res.string.ai_action_prep)
    "ai_action_review" -> stringResource(Res.string.ai_action_review)
    "ai_action_socratic" -> stringResource(Res.string.ai_action_socratic)
    "ai_action_exam" -> stringResource(Res.string.ai_action_exam)
    else -> key
}

@Composable
private fun actionSubtitleString(key: String): String = when (key) {
    "ai_action_prep_sub" -> stringResource(Res.string.ai_action_prep_sub)
    "ai_action_review_sub" -> stringResource(Res.string.ai_action_review_sub)
    "ai_action_socratic_sub" -> stringResource(Res.string.ai_action_socratic_sub)
    "ai_action_exam_sub" -> stringResource(Res.string.ai_action_exam_sub)
    else -> key
}

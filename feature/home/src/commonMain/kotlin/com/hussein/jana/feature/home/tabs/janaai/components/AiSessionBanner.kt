package com.hussein.jana.feature.home.tabs.janaai.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import com.hussein.jana.core.designsystem.ai_session_focus
import com.hussein.jana.core.designsystem.ai_session_greeting
import com.hussein.jana.core.designsystem.ai_session_looming
import com.hussein.jana.feature.home.tabs.janaai.AiChatState
import com.hussein.jana.feature.home.tabs.janaai.AiQuickAction
import org.jetbrains.compose.resources.stringResource

/**
 * Collapsible teal session-looming card.
 *
 * Collapsed by default — shows only the looming badge + chevron arrow.
 * Tap anywhere on the card to toggle expand/collapse with an animated transition.
 * Expand/collapse is local UI state; it does not go through the MVI ViewModel.
 */
@Composable
internal fun AiSessionBanner(
    state: AiChatState,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "banner_chevron",
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ScholarlyTeal,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            // ── Always-visible collapsed row: badge + chevron ─────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = CircleShape,
                    color = AmberGold.copy(alpha = 0.2f),
                ) {
                    Text(
                        text = stringResource(Res.string.ai_session_looming, state.sessionTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(chevronRotation),
                )
            }

            // ── Expandable section — animated ─────────────────────────────────
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit  = shrinkVertically(),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Session greeting headline
                    Text(
                        text = stringResource(
                            Res.string.ai_session_greeting,
                            state.userName,
                            state.sessionSubject,
                            state.sessionTeacher,
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    // Focus description
                    Text(
                        text = stringResource(Res.string.ai_session_focus, state.sessionFocus),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2×2 quick-action grid
                    val actions = state.quickActions
                    if (actions.isNotEmpty()) {
                        Column {
                            actions.chunked(2).forEachIndexed { rowIdx, rowActions ->
                                if (rowIdx > 0) Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    rowActions.forEach { action ->
                                        QuickActionItem(
                                            action = action,
                                            onActionClick = onActionClick,
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    if (rowActions.size < 2) Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
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
            Text(text = action.emoji, style = MaterialTheme.typography.titleSmall)
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

// ── String resource resolvers ─────────────────────────────────────────────────

@Composable
private fun actionTitleString(key: String): String = when (key) {
    "ai_action_prep"     -> stringResource(Res.string.ai_action_prep)
    "ai_action_review"   -> stringResource(Res.string.ai_action_review)
    "ai_action_socratic" -> stringResource(Res.string.ai_action_socratic)
    "ai_action_exam"     -> stringResource(Res.string.ai_action_exam)
    else                 -> ""
}

@Composable
private fun actionSubtitleString(key: String): String = when (key) {
    "ai_action_prep_sub"     -> stringResource(Res.string.ai_action_prep_sub)
    "ai_action_review_sub"   -> stringResource(Res.string.ai_action_review_sub)
    "ai_action_socratic_sub" -> stringResource(Res.string.ai_action_socratic_sub)
    "ai_action_exam_sub"     -> stringResource(Res.string.ai_action_exam_sub)
    else                     -> ""
}

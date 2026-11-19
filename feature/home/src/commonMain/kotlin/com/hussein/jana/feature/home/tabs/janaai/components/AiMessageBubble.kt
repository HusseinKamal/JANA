package com.hussein.jana.feature.home.tabs.janaai.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.AstralIndigo
import com.hussein.jana.core.designsystem.OnBackground
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.PrimaryContainer
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SurfaceContainerHigh
import com.hussein.jana.core.designsystem.SurfaceContainerLow
import com.hussein.jana.core.designsystem.ai_next_step_label
import com.hussein.jana.core.designsystem.ai_typing_indicator
import com.hussein.jana.feature.home.tabs.janaai.AiMessage
import org.jetbrains.compose.resources.stringResource

/**
 * Left-aligned AI message bubble with support for rich layout:
 * - Optional amber [AiMessage.highlightPhrase] pill header
 * - Main [AiMessage.content] text
 * - Optional centred monospace [AiMessage.equationDisplay] surface
 * - Optional [AiMessage.content2] text after the equation
 * - Optional tinted "YOUR NEXT STEP" [AiMessage.nextStepPrompt] box
 * - Timestamp + optional badge pill
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AiMessageBubble(
    msg: AiMessage,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // ── AI avatar ─────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = ScholarlyTeal,
                modifier = Modifier.size(16.dp),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // ── Message bubble — long-press to copy ───────────────────────────────
        Surface(
            modifier = Modifier
                .weight(1f)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { clipboard.setText(AnnotatedString(msg.content)) },
                ),
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            color = SurfaceContainerLow,
            border = BorderStroke(1.dp, OutlineVariant),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Optional highlight phrase pill
                if (msg.highlightPhrase != null) {
                    Surface(
                        shape = CircleShape,
                        color = AmberGold.copy(alpha = 0.15f),
                    ) {
                        Text(
                            text = msg.highlightPhrase,
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberDark,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Main content
                Text(
                    text = msg.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnBackground,
                    lineHeight = 20.sp,
                )

                // Optional equation display
                if (msg.equationDisplay != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceContainerHigh,
                    ) {
                        Text(
                            text = msg.equationDisplay,
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                            color = ScholarlyTeal,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Optional second content paragraph
                if (msg.content2 != null) {
                    Text(
                        text = msg.content2,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnBackground,
                        lineHeight = 20.sp,
                    )
                }

                // Optional "next step" prompt box
                if (msg.nextStepPrompt != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = ScholarlyTeal.copy(alpha = 0.08f),
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = stringResource(Res.string.ai_next_step_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = ScholarlyTeal,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = msg.nextStepPrompt,
                                style = MaterialTheme.typography.bodySmall,
                                color = ScholarlyTeal,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Timestamp + badge row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = msg.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                    )
                    if (msg.badge != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = AstralIndigo.copy(alpha = 0.1f),
                        ) {
                            Text(
                                text = msg.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = AstralIndigo,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Typing indicator ──────────────────────────────────────────────────────────

/**
 * Animated three-dot typing indicator shown while JANA AI is composing a reply.
 *
 * Each dot scales between 0.6 and 1.0 with staggered 150 ms offsets to create
 * a wave effect.
 */
@Composable
internal fun AiTypingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_indicator")

    val scale0 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(0),
        ),
        label = "dot0",
    )
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(150),
        ),
        label = "dot1",
    )
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(300),
        ),
        label = "dot2",
    )

    val typingDesc = stringResource(Res.string.ai_typing_indicator)
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .semantics { contentDescription = typingDesc },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // AI avatar (matches AiMessageBubble)
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = ScholarlyTeal,
                modifier = Modifier.size(16.dp),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            color = SurfaceContainerLow,
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                listOf(scale0, scale1, scale2).forEach { scale ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .clip(CircleShape)
                            .background(ScholarlyTeal.copy(alpha = 0.4f)),
                    )
                }
            }
        }
    }
}

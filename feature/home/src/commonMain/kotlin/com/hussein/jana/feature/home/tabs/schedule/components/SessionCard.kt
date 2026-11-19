package com.hussein.jana.feature.home.tabs.schedule.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.AstralIndigo
import com.hussein.jana.core.designsystem.CardWhite
import com.hussein.jana.core.designsystem.ErrorContainer
import com.hussein.jana.core.designsystem.Error
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.PrimaryContainer
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SuccessGreen
import com.hussein.jana.core.designsystem.SurfaceContainerHigh
import com.hussein.jana.core.designsystem.schedule_ai_socratic
import com.hussein.jana.core.designsystem.schedule_book_seat
import com.hussein.jana.core.designsystem.schedule_calendar_synced
import com.hussein.jana.core.designsystem.schedule_join_live
import com.hussein.jana.core.designsystem.schedule_pdf_available
import com.hussein.jana.core.designsystem.schedule_prep_ai
import com.hussein.jana.core.designsystem.schedule_reserve_credit
import com.hussein.jana.core.designsystem.schedule_review_notes
import com.hussein.jana.core.designsystem.schedule_view_syllabus
import com.hussein.jana.feature.home.tabs.schedule.SessionAction
import com.hussein.jana.feature.home.tabs.schedule.SessionActionStyle
import com.hussein.jana.feature.home.tabs.schedule.SessionSlot
import com.hussein.jana.feature.home.tabs.schedule.SessionStatus
import org.jetbrains.compose.resources.stringResource

/**
 * Card displaying a single [SessionSlot].
 *
 * Layout sections (top → bottom):
 *  1. Badge row — subject chip, optional time chip (TODAY), optional seats chip
 *  2. Session title (Serif font)
 *  3. Teacher avatar + teacher / duration / format row
 *  4. Optional AI note (AstralIndigo with AutoAwesome icon)
 *  5. Optional prerequisite note with optional PDF badge
 *  6. Optional calendar-synced row with View Syllabus text button
 *  7. Optional pricing and pairing notes
 *  8. Action button(s) row
 */
@Composable
internal fun SessionCard(
    session: SessionSlot,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, OutlineVariant),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // ── Top badge row ─────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Subject chip (always visible)
                SubjectBadge(label = session.subject)

                if (session.status == SessionStatus.TODAY) {
                    Spacer(modifier = Modifier.width(6.dp))
                    TimeBadge(time = session.time)
                }

                if (session.seatsLabel != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    SeatsBadge(seatsLabel = session.seatsLabel)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Session title ─────────────────────────────────────────────────
            Text(
                text = session.title,
                style = MaterialTheme.typography.titleMedium,
                color = ScholarlyTeal,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ── Teacher row ───────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                TeacherAvatar(name = session.teacher)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${session.teacher} • ${session.durationMins} mins • ${session.formatLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                )
            }

            // ── AI note ───────────────────────────────────────────────────────
            if (session.aiNote != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = AstralIndigo,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = session.aiNote,
                        style = MaterialTheme.typography.labelSmall,
                        color = AstralIndigo,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // ── Prerequisite note ─────────────────────────────────────────────
            if (session.prerequisiteNote != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = AmberDark,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = session.prerequisiteNote,
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberDark,
                        modifier = Modifier.weight(1f),
                    )
                    if (session.hasPdf) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = PrimaryContainer.copy(alpha = 0.4f),
                        ) {
                            Text(
                                text = stringResource(Res.string.schedule_pdf_available),
                                style = MaterialTheme.typography.labelSmall,
                                color = ScholarlyTeal,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
            }

            // ── Calendar synced indicator ─────────────────────────────────────
            if (session.calendarSynced) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Event,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.schedule_calendar_synced),
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                    )
                }
            }

            // ── Pricing note ──────────────────────────────────────────────────
            if (session.pricingNote != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = session.pricingNote,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(alpha = 0.7f),
                )
            }

            // ── Pairing note ──────────────────────────────────────────────────
            if (session.pairingNote != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = session.pairingNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // ── Action buttons row ────────────────────────────────────────────
            if (session.actions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                ActionButtonsRow(actions = session.actions, onActionClick = onActionClick)
            }
        }
    }
}

// ── Private composables ───────────────────────────────────────────────────────

@Composable
private fun SubjectBadge(label: String) {
    Surface(
        shape = CircleShape,
        color = SurfaceContainerHigh,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun TimeBadge(time: String) {
    Surface(
        shape = CircleShape,
        color = AmberGold.copy(alpha = 0.2f),
    ) {
        Text(
            text = "Starts $time",
            style = MaterialTheme.typography.labelSmall,
            color = AmberDark,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun SeatsBadge(seatsLabel: String) {
    val isUrgent = "Only" in seatsLabel
    Surface(
        shape = CircleShape,
        color = if (isUrgent) ErrorContainer else SurfaceContainerHigh,
    ) {
        Text(
            text = seatsLabel,
            style = MaterialTheme.typography.labelSmall,
            color = if (isUrgent) Error else OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun TeacherAvatar(name: String) {
    Surface(
        modifier = Modifier.size(24.dp),
        shape = CircleShape,
        color = SurfaceContainerHigh,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = name.first().toString(),
                style = MaterialTheme.typography.labelSmall,
                color = ScholarlyTeal,
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    actions: List<SessionAction>,
    onActionClick: (String) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        actions.forEachIndexed { index, action ->
            val label = actionLabelString(action.labelKey)
            val buttonModifier = Modifier.weight(1f)

            when (action.style) {
                SessionActionStyle.PRIMARY_TEAL -> {
                    Button(
                        onClick = { onActionClick(action.id) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = ScholarlyTeal),
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                        )
                    }
                }

                SessionActionStyle.OUTLINE -> {
                    OutlinedButton(
                        onClick = { onActionClick(action.id) },
                        modifier = buttonModifier,
                        border = BorderStroke(1.dp, OutlineVariant),
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = ScholarlyTeal,
                        )
                    }
                }

                SessionActionStyle.AMBER -> {
                    Button(
                        onClick = { onActionClick(action.id) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = ScholarlyTeal,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            if (index < actions.lastIndex) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

/**
 * Resolves a string-resource key from [SessionAction.labelKey] to a localised label.
 *
 * Returns an empty string for unknown keys rather than crashing, following the
 * same defensive pattern used in the Teachers tab.
 */
@Composable
private fun actionLabelString(key: String): String = when (key) {
    "schedule_prep_ai"        -> stringResource(Res.string.schedule_prep_ai)
    "schedule_join_live"      -> stringResource(Res.string.schedule_join_live)
    "schedule_review_notes"   -> stringResource(Res.string.schedule_review_notes)
    "schedule_ai_socratic"    -> stringResource(Res.string.schedule_ai_socratic)
    "schedule_view_syllabus"  -> stringResource(Res.string.schedule_view_syllabus)
    "schedule_reserve_credit" -> stringResource(Res.string.schedule_reserve_credit)
    "schedule_book_seat"      -> stringResource(Res.string.schedule_book_seat)
    else                      -> ""
}

package com.hussein.jana.feature.home.tabs.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.PrimaryContainer
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SurfaceContainerHigh
import com.hussein.jana.core.designsystem.SurfaceContainerLow
import com.hussein.jana.core.designsystem.home_reserved
import com.hussein.jana.core.designsystem.home_upcoming_sessions
import com.hussein.jana.core.designsystem.home_view_calendar
import com.hussein.jana.feature.home.tabs.home.UpcomingSessionData
import org.jetbrains.compose.resources.stringResource

@Composable
fun UpcomingSessionsSection(
    sessions: List<UpcomingSessionData>,
    onViewCalendarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.home_upcoming_sessions),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
                color = ScholarlyTeal,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onViewCalendarClick) {
                Text(
                    text = "${stringResource(Res.string.home_view_calendar)} \u2192",
                    color = AmberDark,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        sessions.forEachIndexed { index, session ->
            UpcomingSessionCard(session = session)
            if (index < sessions.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ── Private card ──────────────────────────────────────────────────────────────

@Composable
private fun UpcomingSessionCard(session: UpcomingSessionData) {
    val initials = teacherInitials(session.teacherName)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceContainerLow,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, OutlineVariant),
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                // Date + time row + reserved badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AmberGold),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${stringResource(session.dayLabelRes)} \u2022 ${session.timeLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    if (session.isReserved) {
                        Surface(
                            color = PrimaryContainer,
                            shape = CircleShape,
                        ) {
                            Text(
                                text = stringResource(Res.string.home_reserved),
                                style = MaterialTheme.typography.labelSmall,
                                color = ScholarlyTeal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = session.courseTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = ScholarlyTeal,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Teacher row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.labelSmall,
                            color = ScholarlyTeal,
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = session.teacherName,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                    )
                }
            }
        }
    }
}

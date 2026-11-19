package com.hussein.jana.feature.home.tabs.schedule.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.LiveRed
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.PrimaryContainer
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SurfaceContainerHigh
import com.hussein.jana.core.designsystem.schedule_open_enrollment
import com.hussein.jana.core.designsystem.schedule_recommended
import com.hussein.jana.core.designsystem.schedule_reserved_seat
import com.hussein.jana.feature.home.tabs.schedule.ScheduleTabState
import com.hussein.jana.feature.home.tabs.schedule.SessionSlot
import com.hussein.jana.feature.home.tabs.schedule.SessionStatus
import org.jetbrains.compose.resources.stringResource

/**
 * Day-group section header displayed above a group of [SessionSlot]s sharing
 * the same [SessionSlot.dayGroupKey].
 *
 * Shows the day display label on the left and a status badge on the right that
 * reflects the group's first session status.
 */
@Composable
internal fun DaySectionHeader(
    session: SessionSlot,
    state: ScheduleTabState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = session.dayDisplayLabel,
            style = MaterialTheme.typography.titleSmall,
            color = ScholarlyTeal,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )

        when (session.status) {
            SessionStatus.TODAY -> {
                Surface(
                    shape = CircleShape,
                    color = LiveRed.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = "in ${session.minutesUntil} mins",
                        style = MaterialTheme.typography.labelSmall,
                        color = LiveRed,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            SessionStatus.TOMORROW,
            SessionStatus.RESERVED,
            -> {
                Surface(
                    shape = CircleShape,
                    color = AmberGold.copy(alpha = 0.15f),
                ) {
                    Text(
                        text = stringResource(Res.string.schedule_reserved_seat),
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberDark,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            SessionStatus.RECOMMENDED -> {
                Surface(
                    shape = CircleShape,
                    color = PrimaryContainer.copy(alpha = 0.5f),
                ) {
                    Text(
                        text = stringResource(Res.string.schedule_recommended),
                        style = MaterialTheme.typography.labelSmall,
                        color = ScholarlyTeal,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            SessionStatus.OPEN_ENROLLMENT -> {
                Surface(
                    shape = CircleShape,
                    color = SurfaceContainerHigh,
                ) {
                    Text(
                        text = stringResource(Res.string.schedule_open_enrollment),
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
        }
    }
}

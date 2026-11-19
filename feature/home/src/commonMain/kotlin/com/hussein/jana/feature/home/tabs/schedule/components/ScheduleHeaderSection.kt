package com.hussein.jana.feature.home.tabs.schedule.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.PrimaryContainer
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.schedule_sync
import com.hussein.jana.core.designsystem.schedule_title
import com.hussein.jana.core.designsystem.schedule_weekly
import com.hussein.jana.feature.home.tabs.schedule.ScheduleTabState
import org.jetbrains.compose.resources.stringResource

/**
 * Sticky header section shown above the scrollable LazyColumn on the Schedule tab.
 *
 * Shows:
 *  - Amber uppercase "WEEKLY SCHEDULE" eyebrow label
 *  - Bold teal "Live Sessions & Labs" headline
 *  - Compact "Sync Calendar" pill button on the right
 */
@Composable
internal fun ScheduleHeaderSection(
    state: ScheduleTabState,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 4.dp),
    ) {
        // Eyebrow label
        Text(
            text = stringResource(Res.string.schedule_weekly).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = AmberDark,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Headline row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.schedule_title),
                style = MaterialTheme.typography.headlineSmall,
                color = ScholarlyTeal,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Sync Calendar pill button
            Surface(
                onClick = onSyncClick,
                shape = CircleShape,
                color = PrimaryContainer,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Sync,
                        contentDescription = null,
                        tint = ScholarlyTeal,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.schedule_sync),
                        style = MaterialTheme.typography.labelSmall,
                        color = ScholarlyTeal,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

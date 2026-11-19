package com.hussein.jana.feature.home.tabs.schedule.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SurfaceContainerLow
import com.hussein.jana.core.designsystem.schedule_apple_calendar
import com.hussein.jana.core.designsystem.schedule_calendar_desc
import com.hussein.jana.core.designsystem.schedule_calendar_title
import com.hussein.jana.core.designsystem.schedule_google_calendar
import org.jetbrains.compose.resources.stringResource

/**
 * Bottom card prompting the student to integrate their device calendar
 * (Google Calendar or Apple iCal).
 */
@Composable
internal fun CalendarIntegrationCard(
    onGoogle: () -> Unit,
    onApple: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerLow,
        border = BorderStroke(1.dp, OutlineVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with sync icon + title
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Sync,
                    contentDescription = null,
                    tint = ScholarlyTeal,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.schedule_calendar_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = ScholarlyTeal,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            Text(
                text = stringResource(Res.string.schedule_calendar_desc),
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar buttons row
            Row(modifier = Modifier.fillMaxWidth()) {
                // Google Calendar
                OutlinedButton(
                    onClick = onGoogle,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, OutlineVariant),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Language,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = ScholarlyTeal,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.schedule_google_calendar),
                        style = MaterialTheme.typography.labelMedium,
                        color = ScholarlyTeal,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Apple iCal
                OutlinedButton(
                    onClick = onApple,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, OutlineVariant),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Smartphone,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = ScholarlyTeal,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.schedule_apple_calendar),
                        style = MaterialTheme.typography.labelMedium,
                        color = ScholarlyTeal,
                    )
                }
            }
        }
    }
}

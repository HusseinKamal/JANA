package com.hussein.jana.feature.home.tabs.schedule.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaWeekStrip
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.schedule_filter_all
import com.hussein.jana.feature.home.tabs.schedule.ScheduleTabState
import org.jetbrains.compose.resources.stringResource

/**
 * Section displaying the month label, the [JanaWeekStrip], and subject filter chips.
 *
 * The filter chip for "all" shows the current filtered session count in its label.
 */
@Composable
internal fun ScheduleWeekSection(
    state: ScheduleTabState,
    onDaySelected: (Int) -> Unit,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Month + location row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.currentMonth,
                style = MaterialTheme.typography.titleSmall,
                color = ScholarlyTeal,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = state.location,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 7-day week strip
        JanaWeekStrip(
            days = state.weekDays,
            selectedDate = state.selectedDate,
            onDaySelected = onDaySelected,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Subject filter chips
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 2.dp),
        ) {
            state.filters.forEachIndexed { index, filter ->
                val isSelected = filter.key == state.selectedFilter
                val chipLabel = if (filter.key == "all") {
                    "${stringResource(Res.string.schedule_filter_all)} (${state.filteredSessions.size})"
                } else {
                    "${filter.label} (${filter.count})"
                }

                Surface(
                    onClick = { onFilterSelected(filter.key) },
                    shape = CircleShape,
                    color = if (isSelected) ScholarlyTeal else Color.Transparent,
                    border = if (isSelected) null else BorderStroke(1.dp, OutlineVariant),
                ) {
                    Text(
                        text = chipLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.White else OnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }

                if (index < state.filters.lastIndex) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

package com.hussein.jana.core.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A single day entry for [JanaWeekStrip].
 *
 * @param dayLabel  Short localised day name, e.g. "Mon" or "الإثنين".
 * @param date      Calendar date number, e.g. 21.
 * @param hasSession Whether this day has at least one scheduled session (shows a dot indicator).
 */
data class WeekDay(
    val dayLabel: String,
    val date: Int,
    val hasSession: Boolean = false,
)

/**
 * Reusable 7-day horizontal week calendar strip.
 *
 * Usable on the Schedule tab, AI chat session prep screens, and any screen
 * that needs a compact date selector within the current week.
 *
 * The [days] list is provided by the caller so the labels are already
 * localised (Arabic callers pass "الإثنين", "الثلاثاء", …; English callers
 * pass "Mon", "Tue", …).
 *
 * @param days           Ordered list of [WeekDay] entries to display.
 * @param selectedDate   The currently-selected [WeekDay.date] value.
 * @param onDaySelected  Callback invoked with the [WeekDay.date] when a day is tapped.
 * @param modifier       Optional modifier for the outer [Row].
 */
@Composable
fun JanaWeekStrip(
    days: List<WeekDay>,
    selectedDate: Int,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
    ) {
        days.forEach { day ->
            DayCell(
                day = day,
                selected = day.date == selectedDate,
                onSelected = { onDaySelected(day.date) },
            )
        }
    }
}

// ── Private composable ────────────────────────────────────────────────────────

@Composable
private fun DayCell(
    day: WeekDay,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onSelected)
            .padding(horizontal = 6.dp),
    ) {
        // Day label (Mon, Tue … or الإثنين …)
        Text(
            text = day.dayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) ScholarlyTeal else OnSurfaceVariant,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Date circle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .then(
                    if (selected) Modifier else Modifier,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = if (selected) ScholarlyTeal else Color.Transparent,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${day.date}",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (selected) Color.White else OnBackground,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Session dot indicator
        if (day.hasSession) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .then(
                        // Using Surface for background colour isn't ideal at 4dp; use Box + background
                        Modifier,
                    ),
            ) {
                Surface(
                    modifier = Modifier.size(4.dp),
                    shape = CircleShape,
                    color = if (selected) Color.White else AmberGold,
                ) {}
            }
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

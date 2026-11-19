package com.hussein.jana.feature.home.tabs.teachers.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.teachers_filter_all
import com.hussein.jana.core.designsystem.teachers_filter_biology
import com.hussein.jana.core.designsystem.teachers_filter_chemistry
import com.hussein.jana.core.designsystem.teachers_filter_english
import com.hussein.jana.core.designsystem.teachers_filter_math
import com.hussein.jana.core.designsystem.teachers_filter_physics
import com.hussein.jana.feature.home.tabs.teachers.SubjectFilter
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Horizontally scrollable row of subject filter chips.
 *
 * Uses a plain [Row] with [horizontalScroll] (not LazyRow) for the small,
 * fixed number of filter items. Chip labels are resolved from string resources
 * via the filter [SubjectFilter.key].
 */
@Composable
fun SubjectFilterRow(
    filters: List<SubjectFilter>,
    selectedKey: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 2.dp),
    ) {
        filters.forEachIndexed { index, filter ->
            val isSelected = filter.key == selectedKey
            val label = stringResource(filterLabelResource(filter.key))

            Surface(
                onClick = { onFilterSelected(filter.key) },
                shape = CircleShape,
                color = if (isSelected) ScholarlyTeal else Color.Transparent,
                border = if (isSelected) null else BorderStroke(1.dp, OutlineVariant),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) Color.White else OnSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }

            if (index < filters.lastIndex) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

// ── Private helpers ───────────────────────────────────────────────────────────

private fun filterLabelResource(key: String): StringResource = when (key) {
    "all"         -> Res.string.teachers_filter_all
    "mathematics" -> Res.string.teachers_filter_math
    "physics"     -> Res.string.teachers_filter_physics
    "chemistry"   -> Res.string.teachers_filter_chemistry
    "biology"     -> Res.string.teachers_filter_biology
    "english"     -> Res.string.teachers_filter_english
    // Adding a new SubjectFilter without a matching branch here is a known gap —
    // fall back to the "All Subjects" label rather than crashing the UI thread.
    else          -> Res.string.teachers_filter_all
}

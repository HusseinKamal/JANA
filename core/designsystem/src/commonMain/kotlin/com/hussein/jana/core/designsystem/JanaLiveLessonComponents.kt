package com.hussein.jana.core.designsystem

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A pill-shaped status chip with a customisable container and text colour.
 * Used for lesson statuses: Upcoming, Live Now, Ended.
 */
@Composable
fun JanaStatusChip(
    status: String,
    containerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
        )
    }
}

/**
 * A compact date/time label with a leading calendar icon.
 * [formattedDateTime] should already be a localised, human-readable string.
 */
@Composable
fun JanaDateTimeLabel(
    formattedDateTime: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = OnSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = formattedDateTime,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
        )
    }
}

/**
 * An icon that represents a file type based on its extension string.
 * Supported: "pdf", "docx" / "doc", "pptx" / "ppt". All others get a
 * generic drive-file icon.
 */
@Composable
fun JanaFileTypeIcon(
    fileType: String,
    modifier: Modifier = Modifier,
) {
    val (icon, tint) = when (fileType.lowercase()) {
        "pdf" -> Icons.Rounded.PictureAsPdf to LiveRed
        "docx", "doc" -> Icons.Rounded.Description to AstralIndigo
        "pptx", "ppt" -> Icons.Rounded.Slideshow to AmberGold
        else -> Icons.Rounded.InsertDriveFile to OnSurfaceVariant
    }
    Icon(
        imageVector = icon,
        contentDescription = fileType,
        modifier = modifier,
        tint = tint,
    )
}

/**
 * A section header row with an optional trailing action button.
 * Uses [title] rendered in titleSmall + SemiBold on the start, and
 * an optional [TextButton] labelled [actionLabel] on the end.
 */
@Composable
fun JanaSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

/**
 * An amber pill badge that shows a countdown until a lesson starts.
 * Pass a pre-formatted [formattedText] (e.g. "Starts in 15 min") so that
 * string resource formatting is handled at the call site, keeping this
 * composable free of resource lookups.
 */
@Composable
fun JanaCountdownBadge(
    formattedText: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = AmberLight,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Timer,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = AmberDark,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formattedText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = AmberDark,
            )
        }
    }
}

/**
 * A styled inner [TabRow] with a primary-coloured indicator.
 * Use for content tabs *within* a screen (not the bottom navigation).
 *
 * @param tabs     Ordered list of display labels.
 * @param selectedIndex  Index of the currently selected tab.
 * @param onTabSelected  Called with the new index when the user taps a tab.
 */
@Composable
fun JanaInnerTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
    ) {
        tabs.forEachIndexed { index, label ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (index == selectedIndex) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (index == selectedIndex) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                },
            )
        }
    }
}

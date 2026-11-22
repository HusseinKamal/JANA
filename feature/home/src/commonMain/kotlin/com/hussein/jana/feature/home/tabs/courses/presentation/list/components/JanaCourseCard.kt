package com.hussein.jana.feature.home.tabs.courses.presentation.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaLockedBadge
import com.hussein.jana.core.designsystem.JanaPriceTag
import com.hussein.jana.core.designsystem.JanaStatusChip
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SurfaceContainerLowest
import com.hussein.jana.core.designsystem.course_continue_btn
import com.hussein.jana.core.designsystem.course_locked
import com.hussein.jana.core.designsystem.course_progress_label
import com.hussein.jana.core.designsystem.course_start_btn
import com.hussein.jana.core.designsystem.course_subscribe_btn
import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.presentation.common.formatted
import org.jetbrains.compose.resources.stringResource

/**
 * Card showing a course summary: title, subject chips and either
 * - subscribed: progress bar + Start/Continue (opens details), or
 * - locked: lock overlay, Locked chip, price + Subscribe (opens details with checkout).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JanaCourseCard(
    course: Course,
    onClick: () -> Unit,
    onSubscribeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLocked = !course.isSubscribed

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Course thumbnail placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = course.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subject chips
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                course.subjects.forEach { subject ->
                    JanaStatusChip(
                        status = subject,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLocked) {
                LockedFooter(course = course, onSubscribeClick = onSubscribeClick)
            } else {
                SubscribedFooter(course = course, onClick = onClick)
            }
        }
    }
}

@Composable
private fun LockedFooter(
    course: Course,
    onSubscribeClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        JanaLockedBadge(text = stringResource(Res.string.course_locked))
        course.price?.let { JanaPriceTag(priceText = it.formatted()) }
    }

    Spacer(modifier = Modifier.height(12.dp))

    CardActionButton(
        label = stringResource(Res.string.course_subscribe_btn),
        onClick = onSubscribeClick,
    )
}

@Composable
private fun SubscribedFooter(
    course: Course,
    onClick: () -> Unit,
) {
    val progress = if (course.totalLessons > 0) {
        course.completedLessons.toFloat() / course.totalLessons.toFloat()
    } else {
        0f
    }
    val buttonLabel = if (course.completedLessons > 0) {
        stringResource(Res.string.course_continue_btn)
    } else {
        stringResource(Res.string.course_start_btn)
    }
    val progressLabel = stringResource(Res.string.course_progress_label)
        .replace("%1\$d", course.completedLessons.toString())
        .replace("%2\$d", course.totalLessons.toString())

    // Progress bar + label
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth(),
        color = ScholarlyTeal,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = progressLabel,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Spacer(modifier = Modifier.height(12.dp))

    CardActionButton(label = buttonLabel, onClick = onClick)
}

@Composable
private fun CardActionButton(
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = ScholarlyTeal),
        shape = RoundedCornerShape(10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

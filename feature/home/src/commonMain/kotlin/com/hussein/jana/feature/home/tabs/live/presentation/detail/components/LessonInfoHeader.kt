package com.hussein.jana.feature.home.tabs.live.presentation.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.AmberLight
import com.hussein.jana.core.designsystem.JanaDateTimeLabel
import com.hussein.jana.core.designsystem.JanaStatusChip
import com.hussein.jana.core.designsystem.LiveRed
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.live_status_ended
import com.hussein.jana.core.designsystem.live_status_live_now
import com.hussein.jana.core.designsystem.live_status_upcoming
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonStatus
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson
import org.jetbrains.compose.resources.stringResource

@Composable
fun LessonInfoHeader(
    lesson: LiveLesson,
    modifier: Modifier = Modifier,
) {
    val (statusTextRes, containerColor, textColor) = when (lesson.status) {
        LessonStatus.LIVE_NOW -> Triple(
            Res.string.live_status_live_now,
            LiveRed.copy(alpha = 0.15f),
            LiveRed,
        )
        LessonStatus.ENDED -> Triple(
            Res.string.live_status_ended,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LessonStatus.UPCOMING -> Triple(
            Res.string.live_status_upcoming,
            AmberLight,
            AmberDark,
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            JanaStatusChip(
                status = stringResource(statusTextRes),
                containerColor = containerColor,
                textColor = textColor,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${lesson.subject} • ${lesson.level}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = lesson.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = lesson.teacherName,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        JanaDateTimeLabel(
            formattedDateTime = "${lesson.dateText} • ${lesson.timeText}",
        )

        if (lesson.description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = lesson.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

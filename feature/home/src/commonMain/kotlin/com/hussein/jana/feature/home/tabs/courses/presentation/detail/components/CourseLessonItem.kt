package com.hussein.jana.feature.home.tabs.courses.presentation.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaStatusChip
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SuccessGreen
import com.hussein.jana.core.designsystem.course_exam_chip
import com.hussein.jana.core.designsystem.course_lesson_duration
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseLesson
import org.jetbrains.compose.resources.stringResource

@Composable
fun CourseLessonItem(
    lesson: CourseLesson,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val durationLabel = stringResource(Res.string.course_lesson_duration)
        .replace("%1\$d", lesson.durationMinutes.toString())

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = if (lesson.isCompleted) {
                Icons.Rounded.CheckCircle
            } else {
                Icons.Rounded.PlayCircleOutline
            },
            contentDescription = null,
            tint = if (lesson.isCompleted) SuccessGreen else ScholarlyTeal,
            modifier = Modifier.size(20.dp),
        )

        Text(
            text = lesson.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )

        if (lesson.hasExam) {
            JanaStatusChip(
                status = stringResource(Res.string.course_exam_chip),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                textColor = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = durationLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

package com.hussein.jana.feature.home.tabs.profile.presentation.syllabus.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaLabeledProgressBar
import com.hussein.jana.core.designsystem.JanaSettingsSection
import com.hussein.jana.feature.home.tabs.profile.presentation.common.lessonsProgressText
import com.hussein.jana.feature.home.tabs.profile.presentation.common.percentText

/** Course title with its lesson progress; tapping the progress opens the course. */
@Composable
fun SyllabusCourseHeader(
    title: String,
    completedLessons: Int,
    totalLessons: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fraction = if (totalLessons > 0) completedLessons.toFloat() / totalLessons else 0f
    JanaSettingsSection(title = null, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            JanaLabeledProgressBar(
                title = lessonsProgressText(completedLessons, totalLessons),
                valueText = fraction.percentText(),
                progress = fraction,
                onClick = onClick,
            )
        }
    }
}

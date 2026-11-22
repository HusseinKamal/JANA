package com.hussein.jana.feature.home.tabs.live.presentation.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.AmberLight
import com.hussein.jana.core.designsystem.JanaStatusChip
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.lesson_exam_open
import com.hussein.jana.core.designsystem.lesson_exam_start
import com.hussein.jana.core.designsystem.lesson_exam_timed
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonExam
import org.jetbrains.compose.resources.stringResource

@Composable
fun LessonExamCard(
    exam: LessonExam,
    onStartExamClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onStartExamClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = null,
                    tint = ScholarlyTeal,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = exam.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (exam.isTimed) {
                    JanaStatusChip(
                        status = "${stringResource(Res.string.lesson_exam_timed)} (${exam.durationMinutes}m)",
                        containerColor = AmberLight,
                        textColor = AmberDark,
                    )
                } else {
                    JanaStatusChip(
                        status = stringResource(Res.string.lesson_exam_open),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStartExamClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ScholarlyTeal),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = stringResource(Res.string.lesson_exam_start),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

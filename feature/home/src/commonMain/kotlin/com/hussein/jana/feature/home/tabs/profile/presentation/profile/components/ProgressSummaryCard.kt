package com.hussein.jana.feature.home.tabs.profile.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaEmptyState
import com.hussein.jana.core.designsystem.JanaErrorState
import com.hussein.jana.core.designsystem.JanaLoadingState
import com.hussein.jana.core.designsystem.JanaProgressRing
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.error_generic
import com.hussein.jana.core.designsystem.profile_progress_empty
import com.hussein.jana.core.designsystem.profile_progress_overall
import com.hussein.jana.feature.home.tabs.profile.domain.model.LearningProgress
import com.hussein.jana.feature.home.tabs.profile.presentation.common.lessonsProgressText
import com.hussein.jana.feature.home.tabs.profile.presentation.common.percentText
import org.jetbrains.compose.resources.stringResource

/**
 * Overall progress ring with the completed-lessons count. Place it at the top of a
 * settings section card; it shows its own loading, error and empty states.
 */
@Composable
fun ProgressSummaryCard(
    isLoading: Boolean,
    isError: Boolean,
    progress: LearningProgress?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        isLoading -> JanaLoadingState(modifier = modifier)
        isError || progress == null -> JanaErrorState(
            message = stringResource(Res.string.error_generic),
            retryLabel = stringResource(Res.string.btn_retry),
            onRetry = onRetry,
            modifier = modifier,
        )
        progress.courses.isEmpty() -> JanaEmptyState(
            message = stringResource(Res.string.profile_progress_empty),
            modifier = modifier,
        )
        else -> Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            JanaProgressRing(
                progress = progress.overallFraction,
                label = progress.overallFraction.percentText(),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(Res.string.profile_progress_overall),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = lessonsProgressText(progress.completedLessons, progress.totalLessons),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

package com.hussein.jana.feature.home.tabs.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberLight
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.PrimaryContainer
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SurfaceContainerHigh
import com.hussein.jana.core.designsystem.home_developing
import com.hussein.jana.core.designsystem.home_foundations
import com.hussein.jana.core.designsystem.home_proficient
import com.hussein.jana.feature.home.tabs.home.SubjectMastery
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubjectProgressRow(
    subject: SubjectMastery,
    modifier: Modifier = Modifier,
) {
    val (chipBackground, levelText) = when (subject.levelKey) {
        "Proficient" -> PrimaryContainer.copy(alpha = 0.30f) to stringResource(Res.string.home_proficient)
        "Developing" -> AmberLight to stringResource(Res.string.home_developing)
        else         -> SurfaceContainerHigh to stringResource(Res.string.home_foundations)
    }

    // Resolve localised display strings in composable scope.
    val subjectName = stringResource(subject.nameRes)
    val trendText = subject.trendRes?.let { stringResource(it) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressArc(progress = subject.progress, size = 52.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subjectName,
                style = MaterialTheme.typography.titleSmall,
                color = ScholarlyTeal,
                fontWeight = FontWeight.SemiBold,
            )
            if (!trendText.isNullOrEmpty()) {
                Text(
                    text = trendText,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = chipBackground,
                shape = CircleShape,
            ) {
                Text(
                    text = levelText,
                    style = MaterialTheme.typography.labelSmall,
                    color = ScholarlyTeal,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
            }
        }
    }
}

// ── Circular progress arc drawn on Canvas ─────────────────────────────────────

@Composable
fun CircularProgressArc(
    progress: Float,
    size: Dp = 52.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = 4.dp.toPx()
            val padding = strokeWidthPx / 2f
            val arcTopLeft = Offset(padding, padding)
            val arcSize = Size(this.size.width - strokeWidthPx, this.size.height - strokeWidthPx)

            // Background track (full circle)
            drawArc(
                color = OutlineVariant,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )

            // Foreground progress arc, starts at 12 o'clock (-90°)
            drawArc(
                color = ScholarlyTeal,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )
        }

        // Centre percentage label
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = ScholarlyTeal,
        )
    }
}

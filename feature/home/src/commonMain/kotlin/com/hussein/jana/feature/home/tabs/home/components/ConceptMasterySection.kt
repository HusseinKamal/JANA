package com.hussein.jana.feature.home.tabs.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.home_breakdown
import com.hussein.jana.core.designsystem.home_concept_mastery
import com.hussein.jana.core.designsystem.home_concept_subtitle
import com.hussein.jana.feature.home.tabs.home.SubjectMastery
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConceptMasterySection(
    subjects: List<SubjectMastery>,
    onBreakdownClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.home_concept_mastery),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
                color = ScholarlyTeal,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onBreakdownClick) {
                Text(
                    text = "${stringResource(Res.string.home_breakdown)} \u2192",
                    color = AmberDark,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        Text(
            text = stringResource(Res.string.home_concept_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        subjects.forEachIndexed { index, subject ->
            SubjectProgressRow(subject = subject)
            if (index < subjects.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

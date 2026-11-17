package com.hussein.jana.feature.home.tabs.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AstralIndigo
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SurfaceContainerLow
import com.hussein.jana.core.designsystem.home_ai_chip
import com.hussein.jana.core.designsystem.home_formula_sheet
import com.hussein.jana.core.designsystem.home_quick_quiz
import org.jetbrains.compose.resources.stringResource

@Composable
fun AiCompanionCard(
    message: String,
    onQuizClick: () -> Unit,
    onFormulaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceContainerLow,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, OutlineVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // AI chip label
            Surface(
                color = AstralIndigo.copy(alpha = 0.10f),
                shape = CircleShape,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = AstralIndigo,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.home_ai_chip),
                        style = MaterialTheme.typography.labelSmall,
                        color = AstralIndigo,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI message with highlighted key phrases
            Text(
                text = highlightKeyPhrases(message),
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onQuizClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.home_quick_quiz),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                OutlinedButton(
                    onClick = onFormulaClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.home_formula_sheet),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

// ── Helper: highlights academic key phrases in bold ScholarlyTeal ─────────────

private val KEY_PHRASES = listOf("discriminant formulas", "polynomial factoring")

private fun highlightKeyPhrases(message: String) = buildAnnotatedString {
    append(message)
    KEY_PHRASES.forEach { phrase ->
        var start = 0
        while (true) {
            val index = message.indexOf(phrase, start, ignoreCase = true)
            if (index < 0) break
            addStyle(
                style = SpanStyle(color = ScholarlyTeal, fontWeight = FontWeight.Bold),
                start = index,
                end = index + phrase.length,
            )
            start = index + 1
        }
    }
}

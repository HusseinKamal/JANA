package com.hussein.jana.feature.home.tabs.teachers.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.AmberLight
import com.hussein.jana.core.designsystem.AstralIndigo
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SuccessGreen
import com.hussein.jana.core.designsystem.teachers_attendance_tracking
import com.hussein.jana.core.designsystem.teachers_how_bold1
import com.hussein.jana.core.designsystem.teachers_how_bold2
import com.hussein.jana.core.designsystem.teachers_how_desc_part1
import com.hussein.jana.core.designsystem.teachers_how_desc_part2
import com.hussein.jana.core.designsystem.teachers_how_desc_part3
import com.hussein.jana.core.designsystem.teachers_how_title
import com.hussein.jana.core.designsystem.teachers_learn_more
import org.jetbrains.compose.resources.stringResource

/**
 * "How JANA Subscriptions Work" informational card shown at the bottom of the
 * Teachers tab. Uses an AnnotatedString body with two bold/coloured spans.
 */
@Composable
fun HowJanaWorksCard(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = AmberLight.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Title row ────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🏮",
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.teachers_how_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = ScholarlyTeal,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Body text with inline bold spans ─────────────────────────────
            val part1 = stringResource(Res.string.teachers_how_desc_part1)
            val bold1 = stringResource(Res.string.teachers_how_bold1)
            val part2 = stringResource(Res.string.teachers_how_desc_part2)
            val bold2 = stringResource(Res.string.teachers_how_bold2)
            val part3 = stringResource(Res.string.teachers_how_desc_part3)

            val bodyText = buildAnnotatedString {
                append(part1)
                withStyle(
                    SpanStyle(
                        color = ScholarlyTeal,
                        fontWeight = FontWeight.SemiBold,
                    ),
                ) { append(bold1) }
                append(part2)
                withStyle(
                    SpanStyle(
                        color = AstralIndigo,
                        fontWeight = FontWeight.SemiBold,
                    ),
                ) { append(bold2) }
                append(part3)
            }

            Text(
                text = bodyText,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                lineHeight = 20.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Attendance tracking row ───────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(Res.string.teachers_attendance_tracking),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(4.dp))
                // "Learn more" is disabled until the information destination is wired.
                // Using OnSurfaceVariant color so disabled state is visually apparent.
                TextButton(onClick = {}, enabled = false) {
                    Text(
                        text = "${stringResource(Res.string.teachers_learn_more)} →",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant.copy(alpha = 0.5f),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

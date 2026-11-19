package com.hussein.jana.feature.home.tabs.teachers.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.CardWhite
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SurfaceContainerHigh
import com.hussein.jana.core.designsystem.teachers_class_format
import com.hussein.jana.core.designsystem.teachers_egp
import com.hussein.jana.core.designsystem.teachers_profile_btn
import com.hussein.jana.core.designsystem.teachers_reserve_btn
import com.hussein.jana.core.designsystem.teachers_stats_format
import com.hussein.jana.core.designsystem.teachers_tuition
import com.hussein.jana.feature.home.tabs.teachers.TeacherData
import com.hussein.jana.feature.home.tabs.teachers.formatK
import org.jetbrains.compose.resources.stringResource

/**
 * Card displaying a single teacher's profile summary.
 *
 * Layout sections (top → bottom):
 *  1. Avatar + name + subject + rating
 *  2. Active students & experience stats
 *  3. Badge chips (horizontally scrollable)
 *  4. Schedule row (emoji + type label + schedule string)
 *  5. Format row (school icon + class format)
 *  6. Divider
 *  7. Tuition price + Profile / Reserve buttons
 */
@Composable
fun TeacherCard(
    teacher: TeacherData,
    onProfileClick: () -> Unit,
    onReserveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, OutlineVariant),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Avatar + Name + Subject + Rating ─────────────────────────────
            Row(verticalAlignment = Alignment.Top) {
                TeacherAvatar(name = teacher.name)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = teacher.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = ScholarlyTeal,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = teacher.subjectDetail,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${teacher.rating} (${teacher.reviewCount})",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Stats row ────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Group,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(
                        Res.string.teachers_stats_format,
                        teacher.activeStudents.formatK(),
                        teacher.yearsExp,
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Badge chips ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                teacher.badges.forEachIndexed { index, badge ->
                    TeacherBadgeChip(badge = badge)
                    if (index < teacher.badges.lastIndex) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Schedule row ─────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = teacher.sessionTypeIcon,
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${teacher.sessionTypeLabel}:",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = teacher.scheduleLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = ScholarlyTeal,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Format row ───────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.School,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${stringResource(Res.string.teachers_class_format)}: ${teacher.sessionFormatLabel}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = OutlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // ── Price + buttons row ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Price column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.teachers_tuition),
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = stringResource(Res.string.teachers_egp),
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${teacher.priceEgp}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ScholarlyTeal,
                            fontWeight = FontWeight.Black,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = teacher.priceSubLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                        )
                    }
                    Text(
                        text = teacher.priceNote,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant.copy(alpha = 0.7f),
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Profile button — wrapContentWidth prevents Arabic labels from squeezing the price column
                OutlinedButton(
                    onClick = onProfileClick,
                    border = BorderStroke(1.dp, OutlineVariant),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.teachers_profile_btn),
                        style = MaterialTheme.typography.labelMedium,
                        color = ScholarlyTeal,
                        maxLines = 1,
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Reserve button — compact padding prevents Arabic text from overflowing
                Button(
                    onClick = onReserveClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ScholarlyTeal),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.teachers_reserve_btn),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

// ── Private helpers ───────────────────────────────────────────────────────────

@Composable
private fun TeacherAvatar(
    name: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(52.dp),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceContainerHigh,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = name.first().toString(),
                style = MaterialTheme.typography.titleMedium,
                color = ScholarlyTeal,
            )
        }
    }
}

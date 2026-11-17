package com.hussein.jana.feature.home.tabs.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.rounded.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.LiveRed
import com.hussein.jana.core.designsystem.PrimaryContainer
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.home_enrolled
import com.hussein.jana.core.designsystem.home_group_session
import com.hussein.jana.core.designsystem.home_live_badge
import com.hussein.jana.core.designsystem.home_min_suffix
import com.hussein.jana.core.designsystem.home_prepare_join
import com.hussein.jana.core.designsystem.home_today
import com.hussein.jana.feature.home.tabs.home.LiveSessionData
import org.jetbrains.compose.resources.stringResource

@Composable
fun LiveSessionCard(
    session: LiveSessionData,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ScholarlyTeal,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Live badge + group session info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LiveZoomPill(minsUntilStart = session.minsUntilStart)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${stringResource(Res.string.home_group_session)} " +
                        "(${session.enrolledCount}/${session.totalSeats} ${stringResource(Res.string.home_enrolled)})",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subject · Level
            Text(
                text = "${session.subject} \u2022 ${session.level}",
                style = MaterialTheme.typography.labelSmall,
                color = AmberGold,
                letterSpacing = 1.sp,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Course title (Newsreader / Serif)
            Text(
                text = session.courseTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Teacher row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Avatar
                val initials = teacherInitials(session.teacherName)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.labelSmall,
                        color = ScholarlyTeal,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Name + rating
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.teacherName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "\u2605 ${session.teacherRating} (${session.teacherReviews} reviews)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f),
                    )
                }
                // Today + time range
                Column(horizontalAlignment = Alignment.End) {
                    if (session.isToday) {
                        Text(
                            text = stringResource(Res.string.home_today),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f),
                        )
                    }
                    Text(
                        text = session.timeRange,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Join button
            Button(
                onClick = onJoinClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = ScholarlyTeal,
                ),
            ) {
                Icon(
                    imageVector = Icons.Rounded.VideoCall,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.home_prepare_join),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

// ── Private helpers ───────────────────────────────────────────────────────────

@Composable
private fun LiveZoomPill(minsUntilStart: Int) {
    Surface(
        shape = CircleShape,
        color = LiveRed,
    ) {
        Text(
            text = "${stringResource(Res.string.home_live_badge)} $minsUntilStart ${stringResource(Res.string.home_min_suffix)}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

/** Extract up to 2 uppercase initials, skipping honorifics like "Dr." and "Eng." */
internal fun teacherInitials(name: String): String =
    name.split(" ")
        .filter { it.length > 2 && it.last() != '.' }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifEmpty { name.take(1).uppercase() }

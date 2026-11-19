package com.hussein.jana.feature.home.tabs.schedule.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.schedule_credits_available
import com.hussein.jana.core.designsystem.schedule_credits_renews
import com.hussein.jana.core.designsystem.schedule_credits_reserved
import com.hussein.jana.core.designsystem.schedule_top_up
import org.jetbrains.compose.resources.stringResource

/**
 * Full-width teal card displaying the student's available session credits,
 * reserved-credit count, and a Top Up button.
 */
@Composable
internal fun ScheduleCreditsBanner(
    creditsAvailable: Int,
    creditsReserved: Int,
    onTopUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ScholarlyTeal,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$creditsAvailable ${stringResource(Res.string.schedule_credits_available)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$creditsReserved ${stringResource(Res.string.schedule_credits_reserved)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.75f),
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(Res.string.schedule_credits_renews),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.65f),
                )
            }

            // Top Up button
            Surface(
                onClick = onTopUp,
                shape = RoundedCornerShape(50),
                color = AmberGold,
            ) {
                Text(
                    text = stringResource(Res.string.schedule_top_up),
                    style = MaterialTheme.typography.labelMedium,
                    color = ScholarlyTeal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }
    }
}

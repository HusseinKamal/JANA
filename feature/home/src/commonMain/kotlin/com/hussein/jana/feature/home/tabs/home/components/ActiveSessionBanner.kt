package com.hussein.jana.feature.home.tabs.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.home_active_session_msg
import com.hussein.jana.core.designsystem.home_minutes
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActiveSessionBanner(
    minsUntilStart: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(AmberGold),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(Res.string.home_active_session_msg),
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$minsUntilStart ${stringResource(Res.string.home_minutes)}",
            style = MaterialTheme.typography.bodySmall,
            color = AmberGold,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

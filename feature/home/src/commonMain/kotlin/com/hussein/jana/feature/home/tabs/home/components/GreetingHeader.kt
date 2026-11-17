package com.hussein.jana.feature.home.tabs.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.home_greeting_morning
import org.jetbrains.compose.resources.stringResource

@Composable
fun GreetingHeader(
    userName: String,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // weight(1f) is a RowScope extension – accessed via the Row lambda receiver
        Text(
            text = "${stringResource(Res.string.home_greeting_morning)}, $userName 👋",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ScholarlyTeal,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onNotificationClick) {
            Icon(
                imageVector = Icons.Rounded.NotificationsNone,
                contentDescription = null,
                tint = OnSurfaceVariant,
            )
        }
    }
}

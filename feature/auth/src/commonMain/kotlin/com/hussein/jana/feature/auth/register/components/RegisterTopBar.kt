package com.hussein.jana.feature.auth.register.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaLogoCard
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.app_name
import com.hussein.jana.core.designsystem.nav_back
import org.jetbrains.compose.resources.stringResource

/**
 * Top app bar for the registration screen.
 * Back arrow + JANA logo card (28dp) + app name text.
 * Handles status bar inset internally — no extra top padding needed.
 */
@Composable
fun RegisterTopBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(Res.string.nav_back),
                tint = OnSurfaceVariant,
            )
        }
        JanaLogoCard(size = 28.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            color = ScholarlyTeal,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

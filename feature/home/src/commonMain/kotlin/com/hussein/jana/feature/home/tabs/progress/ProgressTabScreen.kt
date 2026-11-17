package com.hussein.jana.feature.home.tabs.progress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.home_progress_coming_soon
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProgressTabScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.home_progress_coming_soon),
            style = MaterialTheme.typography.headlineSmall,
            color = OnSurfaceVariant,
        )
    }
}

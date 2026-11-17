package com.hussein.jana.feature.auth.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.MutedSand
import com.hussein.jana.core.designsystem.ScholarlyTeal

@Composable
fun CountryCodeChip(
    modifier: Modifier = Modifier,
    text: String = "EG +20 ▾",
) {
    Surface(
        shape = CircleShape,
        color = MutedSand,
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = ScholarlyTeal,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

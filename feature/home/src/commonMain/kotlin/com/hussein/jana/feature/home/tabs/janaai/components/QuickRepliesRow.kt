package com.hussein.jana.feature.home.tabs.janaai.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.feature.home.tabs.janaai.QuickReply

/**
 * Horizontally scrollable row of quick-reply suggestion chips shown above the input bar.
 *
 * @param replies      List of suggestion chips to display.
 * @param onReplyClick Called with the [QuickReply.id] when a chip is tapped.
 */
@Composable
internal fun QuickRepliesRow(
    replies: List<QuickReply>,
    onReplyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (replies.isEmpty()) return

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        replies.forEach { reply ->
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant),
                onClick = { onReplyClick(reply.id) },
            ) {
                Text(
                    text = reply.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = ScholarlyTeal,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }
    }
}

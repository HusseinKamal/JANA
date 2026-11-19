package com.hussein.jana.feature.home.tabs.janaai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.hussein.jana.core.designsystem.AstralIndigo
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SuccessGreen
import com.hussein.jana.core.designsystem.VellumCanvas
import com.hussein.jana.core.designsystem.ai_active_status
import com.hussein.jana.core.designsystem.ai_arabic_name
import com.hussein.jana.core.designsystem.ai_connected_history
import com.hussein.jana.core.designsystem.ai_menu_cd
import com.hussein.jana.core.designsystem.ai_rag_active
import com.hussein.jana.core.designsystem.ai_stats_format
import com.hussein.jana.core.designsystem.ai_title
import com.hussein.jana.core.designsystem.ai_tutor_label
import com.hussein.jana.feature.home.tabs.janaai.AiChatState
import org.jetbrains.compose.resources.stringResource

/**
 * Sticky header for the AI Chat screen.
 *
 * Displays brand identity (JANA AI + Arabic name), RAG status row, and
 * connection/active badge pills.
 */
@Composable
internal fun AiChatHeader(
    state: AiChatState,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(VellumCanvas)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        // ── Top row: brand identity + menu ───────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Brand left side
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = AstralIndigo,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(Res.string.ai_title),
                style = MaterialTheme.typography.titleSmall,
                color = ScholarlyTeal,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(Res.string.ai_tutor_label),
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Right side: Arabic name + menu
            Text(
                text = stringResource(Res.string.ai_arabic_name),
                style = MaterialTheme.typography.bodySmall,
                color = AmberGold,
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(Res.string.ai_menu_cd),
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── RAG active row ────────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = AstralIndigo,
                modifier = Modifier.size(10.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(Res.string.ai_rag_active),
                style = MaterialTheme.typography.labelSmall,
                color = AstralIndigo,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Status badge pills ────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = ScholarlyTeal,
            ) {
                Text(
                    text = stringResource(Res.string.ai_connected_history),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = SuccessGreen.copy(alpha = 0.15f),
            ) {
                Text(
                    text = stringResource(Res.string.ai_active_status),
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreen,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Stats text ────────────────────────────────────────────────────────
        Text(
            text = stringResource(
                Res.string.ai_stats_format,
                state.sessionCount,
                state.activeSubjectsCount,
            ),
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
        )

        HorizontalDivider(
            color = OutlineVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

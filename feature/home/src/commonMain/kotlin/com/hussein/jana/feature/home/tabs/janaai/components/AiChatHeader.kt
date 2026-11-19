package com.hussein.jana.feature.home.tabs.janaai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.AstralIndigo
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.VellumCanvas
import com.hussein.jana.core.designsystem.ai_arabic_name
import com.hussein.jana.core.designsystem.ai_menu_cd
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
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        // Single compact brand row — RAG/badges/stats removed to maximize chat space
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = AstralIndigo,
                modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(Res.string.ai_title),
                style = MaterialTheme.typography.labelLarge,
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

            Text(
                text = stringResource(Res.string.ai_arabic_name),
                style = MaterialTheme.typography.labelSmall,
                color = AmberGold,
            )
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(Res.string.ai_menu_cd),
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        HorizontalDivider(
            color = OutlineVariant,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

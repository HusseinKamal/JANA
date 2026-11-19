package com.hussein.jana.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource

/**
 * Reusable stateless chat input bar shared across JANA chat screens.
 *
 * Layout: [mic?] [text field with hint] [send button]
 *
 * @param text          Current input value.
 * @param onTextChange  Called when the text changes.
 * @param onSendClick   Called when send is tapped. Only active when [text] is non-blank.
 * @param hint          Placeholder string shown when [text] is empty.
 * @param modifier      Optional outer modifier.
 * @param onMicClick    If non-null a mic icon button is shown; null hides it and shows a small spacer.
 * @param enabled       When false the entire field is disabled.
 */
@Composable
fun JanaChatInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    onMicClick: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        color = SurfaceContainerLow,
        border = BorderStroke(1.dp, OutlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Mic button (optional) ─────────────────────────────────────────
            if (onMicClick != null) {
                IconButton(
                    onClick = onMicClick,
                    enabled = enabled,
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Mic,
                        contentDescription = stringResource(Res.string.input_mic_cd),
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(12.dp))
            }

            // ── Text field with decorative hint ──────────────────────────────
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = enabled,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = OnBackground),
                decorationBox = { innerTextField ->
                    Box {
                        if (text.isEmpty()) {
                            Text(
                                text = hint,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant.copy(alpha = 0.6f),
                            )
                        }
                        innerTextField()
                    }
                },
            )

            Spacer(modifier = Modifier.width(4.dp))

            // ── Send button ───────────────────────────────────────────────────
            val sendActive = text.isNotBlank() && enabled
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (sendActive) ScholarlyTeal else OutlineVariant),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick = onSendClick,
                    enabled = sendActive,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = stringResource(Res.string.input_send_cd),
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

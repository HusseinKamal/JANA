package com.hussein.jana.core.designsystem

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * Confirmation dialog. Visibility is hoisted: render it only while shown.
 * [isDestructive] colors the confirm action with the error color. While [isConfirmLoading]
 * the confirm action shows a spinner and the dialog cannot be dismissed.
 */
@Composable
fun JanaConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
    isConfirmLoading: Boolean = false,
) {
    val canDismiss = !isConfirmLoading
    AlertDialog(
        onDismissRequest = { if (canDismiss) onDismiss() },
        modifier = modifier,
        properties = DialogProperties(
            dismissOnBackPress = canDismiss,
            dismissOnClickOutside = canDismiss,
        ),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isConfirmLoading) {
                if (isConfirmLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = confirmLabel,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDestructive) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isConfirmLoading) {
                Text(text = dismissLabel)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

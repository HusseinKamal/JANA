package com.hussein.jana.core.designsystem

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val TextFieldShape = RoundedCornerShape(12.dp)

/**
 * Labelled, bordered text field. Shows [errorText] under the field and an error border when set.
 *
 * Pass [textDirection] = [TextDirection.Ltr] for values such as phone numbers, which have no
 * strong-direction characters and would otherwise show their digit groups reversed in RTL.
 * The text stays aligned to the layout's start edge.
 */
@Composable
fun JanaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: ImageVector? = null,
    textDirection: TextDirection = TextDirection.Unspecified,
) {
    val colors = MaterialTheme.colorScheme
    val isError = errorText != null
    val isRtlLayout = LocalLayoutDirection.current == LayoutDirection.Rtl
    // TextAlign.Start follows the text direction, so a forced direction needs an absolute
    // alignment to stay on the layout's start edge.
    val textAlign = when {
        textDirection == TextDirection.Unspecified -> TextAlign.Unspecified
        isRtlLayout -> TextAlign.Right
        else -> TextAlign.Left
    }
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isError) colors.error else colors.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isError) colors.error else colors.outlineVariant,
                    shape = TextFieldShape,
                ),
            shape = TextFieldShape,
            color = if (enabled) colors.surface else colors.surfaceVariant,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = if (enabled) colors.onSurface else colors.onSurfaceVariant,
                            textDirection = textDirection,
                            textAlign = textAlign,
                        ),
                        keyboardOptions = keyboardOptions,
                        singleLine = singleLine,
                        cursorBrush = SolidColor(colors.primary),
                    )
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium.copy(textDirection = textDirection),
                            color = colors.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        }
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = colors.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }
    }
}

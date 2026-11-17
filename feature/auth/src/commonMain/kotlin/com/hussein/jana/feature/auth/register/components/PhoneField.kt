package com.hussein.jana.feature.auth.register.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SurfaceContainerLow
import com.hussein.jana.core.designsystem.register_country_code
import com.hussein.jana.feature.auth.components.AuthTextField
import org.jetbrains.compose.resources.stringResource

/**
 * Phone number input field with an "EG +20" country-code chip as leading content.
 * Delegates to [AuthTextField] so border, shape, and styling stay consistent.
 */
@Composable
fun PhoneField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    AuthTextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        leadingContent = {
            Surface(
                shape = RoundedCornerShape(50),
                color = SurfaceContainerLow,
            ) {
                Text(
                    text = stringResource(Res.string.register_country_code),
                    style = MaterialTheme.typography.labelSmall,
                    color = ScholarlyTeal,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        },
    )
}

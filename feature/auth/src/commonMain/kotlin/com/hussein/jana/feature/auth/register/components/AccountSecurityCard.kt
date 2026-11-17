package com.hussein.jana.feature.auth.register.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaCard
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SuccessGreen
import com.hussein.jana.core.designsystem.register_confirm_password
import com.hussein.jana.core.designsystem.register_create_password
import com.hussein.jana.core.designsystem.register_password_match
import com.hussein.jana.core.designsystem.register_security_title
import com.hussein.jana.core.designsystem.error_passwords_mismatch
import com.hussein.jana.feature.auth.components.AuthTextField
import com.hussein.jana.feature.auth.register.PasswordStrength
import com.hussein.jana.feature.auth.register.RegisterIntent
import com.hussein.jana.feature.auth.register.RegisterState
import org.jetbrains.compose.resources.stringResource

/**
 * "Account Security" card — Create Password (with strength indicator) + Confirm Password.
 */
@Composable
fun AccountSecurityCard(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val passwordMatch   = stringResource(Res.string.register_password_match)
    val passwordMismatch = stringResource(Res.string.error_passwords_mismatch)

    JanaCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {

            // ── Section header ────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = ScholarlyTeal,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.register_security_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = ScholarlyTeal,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // ── Create Password field ─────────────────────────────────────────
            AuthTextField(
                label = stringResource(Res.string.register_create_password),
                value = state.password,
                onValueChange = { onIntent(RegisterIntent.PasswordChanged(it)) },
                placeholder = "••••••••••",
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Key,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                    )
                },
                trailingContent = {
                    IconButton(onClick = { onIntent(RegisterIntent.TogglePasswordVisibility) }) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                            contentDescription = if (state.isPasswordVisible) "Hide password" else "Show password",
                            tint = OnSurfaceVariant,
                        )
                    }
                },
            )

            if (state.passwordError != null) {
                Text(
                    text = state.passwordError,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            // ── Password strength indicator (real-time) ───────────────────────
            if (state.passwordStrength != PasswordStrength.NONE) {
                Spacer(modifier = Modifier.height(8.dp))
                PasswordStrengthIndicator(strength = state.passwordStrength)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Confirm Password field ────────────────────────────────────────
            AuthTextField(
                label = stringResource(Res.string.register_confirm_password),
                value = state.confirmPassword,
                onValueChange = { onIntent(RegisterIntent.ConfirmPasswordChanged(it)) },
                placeholder = "••••••••••",
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Sync,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                    )
                },
                trailingContent = {
                    IconButton(onClick = { onIntent(RegisterIntent.ToggleConfirmPasswordVisibility) }) {
                        Icon(
                            imageVector = if (state.isConfirmPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                            contentDescription = if (state.isConfirmPasswordVisible) "Hide password" else "Show password",
                            tint = OnSurfaceVariant,
                        )
                    }
                },
            )

            // ── Confirm password hint (real-time match / mismatch) ────────────
            if (state.confirmPasswordError != null) {
                Text(
                    text = state.confirmPasswordError,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp),
                )
            } else if (state.confirmPassword.isNotEmpty()) {
                val matches = state.confirmPassword == state.password
                Text(
                    text = if (matches) passwordMatch else passwordMismatch,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (matches) SuccessGreen else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

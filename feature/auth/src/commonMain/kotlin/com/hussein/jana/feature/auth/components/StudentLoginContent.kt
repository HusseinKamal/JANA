package com.hussein.jana.feature.auth.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.JanaLogoCard
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.auth_btn_student
import com.hussein.jana.core.designsystem.auth_forgot_password
import com.hussein.jana.core.designsystem.auth_identifier_hint
import com.hussein.jana.core.designsystem.auth_identifier_label
import com.hussein.jana.core.designsystem.auth_no_account
import com.hussein.jana.core.designsystem.auth_or_continue
import com.hussein.jana.core.designsystem.auth_password_hint
import com.hussein.jana.core.designsystem.auth_password_label
import com.hussein.jana.core.designsystem.auth_platform_code_hint
import com.hussein.jana.core.designsystem.auth_platform_code_label
import com.hussein.jana.core.designsystem.auth_register_link
import com.hussein.jana.core.designsystem.auth_remember_device
import com.hussein.jana.core.designsystem.auth_subtitle_student
import com.hussein.jana.core.designsystem.auth_welcome_student
import com.hussein.jana.core.designsystem.loading
import com.hussein.jana.feature.auth.LoginIntent
import com.hussein.jana.feature.auth.LoginState
import org.jetbrains.compose.resources.stringResource

@Composable
fun StudentLoginContent(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        // Hero — centered JANA logo replaces the generic Person icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        ) {
            JanaLogoCard(size = 72.dp)
        }

        // Headline
        Text(
            text = stringResource(Res.string.auth_welcome_student),
            style = MaterialTheme.typography.headlineMedium,
            color = ScholarlyTeal,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = stringResource(Res.string.auth_subtitle_student),
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Platform code field — identifies which teacher's platform the student is accessing
        AuthTextField(
            label = "${stringResource(Res.string.auth_platform_code_label)} *",
            value = state.platformCode,
            onValueChange = { onIntent(LoginIntent.PlatformCodeChanged(it)) },
            placeholder = stringResource(Res.string.auth_platform_code_hint),
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.School,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Identifier field — plain text, user types phone or email directly
        AuthTextField(
            label = "${stringResource(Res.string.auth_identifier_label)} *",
            value = state.identifier,
            onValueChange = { onIntent(LoginIntent.IdentifierChanged(it)) },
            placeholder = stringResource(Res.string.auth_identifier_hint),
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.AlternateEmail,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        PasswordField(
            label = "${stringResource(Res.string.auth_password_label)} *",
            value = state.password,
            onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
            isVisible = state.isPasswordVisible,
            onToggleVisibility = { onIntent(LoginIntent.TogglePasswordVisibility) },
            placeholder = stringResource(Res.string.auth_password_hint),
            protectedBadge = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Remember device + Forgot password — weight splits the row cleanly on one line
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = state.rememberDevice,
                onCheckedChange = { onIntent(LoginIntent.ToggleRememberDevice) },
            )
            // Weight(1f): takes all space that "Forgot Password?" doesn't need
            Text(
                text = stringResource(Res.string.auth_remember_device),
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            TextButton(onClick = { onIntent(LoginIntent.ForgotPasswordClicked) }) {
                Text(
                    text = stringResource(Res.string.auth_forgot_password),
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberDark,
                    maxLines = 1,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        if (state.error != null) {
            Text(
                text = state.error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        // Sign In button
        Button(
            onClick = { onIntent(LoginIntent.SignInClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = ScholarlyTeal),
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                Text(
                    text = stringResource(Res.string.loading),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
            } else {
                Text(
                    text = "🎓 ${stringResource(Res.string.auth_btn_student)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Divider
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "  ${stringResource(Res.string.auth_or_continue)}  ",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Social login
        SocialLoginRow(
            onGoogleClick = { onIntent(LoginIntent.GoogleSignInClicked) },
            onFacebookClick = { onIntent(LoginIntent.FacebookSignInClicked) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Footer — register link
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.auth_no_account),
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(4.dp))
            val registerText = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = AmberDark,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                    ),
                ) {
                    append(stringResource(Res.string.auth_register_link))
                    append(" →")
                }
            }
            TextButton(
                onClick = { onIntent(LoginIntent.RegisterClicked) },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                Text(text = registerText, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

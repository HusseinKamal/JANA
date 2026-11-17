package com.hussein.jana.feature.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.auth_btn_teacher
import com.hussein.jana.core.designsystem.auth_faculty_email_hint
import com.hussein.jana.core.designsystem.auth_faculty_email_label
import com.hussein.jana.core.designsystem.auth_forgot_password
import com.hussein.jana.core.designsystem.auth_institutional_login
import com.hussein.jana.core.designsystem.auth_keep_session
import com.hussein.jana.core.designsystem.auth_password_label
import com.hussein.jana.core.designsystem.auth_sso_link
import com.hussein.jana.core.designsystem.auth_subtitle_teacher
import com.hussein.jana.core.designsystem.auth_welcome_teacher
import com.hussein.jana.core.designsystem.auth_welcome_teacher_arabic
import com.hussein.jana.feature.auth.LoginIntent
import com.hussein.jana.feature.auth.LoginState
import org.jetbrains.compose.resources.stringResource

@Composable
fun TeacherLoginContent(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        // Welcome headline
        Text(
            text = stringResource(Res.string.auth_welcome_teacher),
            style = MaterialTheme.typography.headlineMedium,
            color = ScholarlyTeal,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Arabic subheadline
        Text(
            text = stringResource(Res.string.auth_welcome_teacher_arabic),
            style = MaterialTheme.typography.headlineSmall,
            color = AmberGold,
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = stringResource(Res.string.auth_subtitle_teacher),
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Tab switcher
        LoginTabSwitcher(
            activeTab = state.activeTab,
            onTabSelected = { onIntent(LoginIntent.TabSelected(it)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Email field with institutional login link
        AuthTextField(
            label = stringResource(Res.string.auth_faculty_email_label),
            value = state.identifier,
            onValueChange = { onIntent(LoginIntent.IdentifierChanged(it)) },
            placeholder = stringResource(Res.string.auth_faculty_email_hint),
            modifier = Modifier.fillMaxWidth(),
            labelContent = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.auth_faculty_email_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = { onIntent(LoginIntent.InstitutionalSSOClicked) },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.auth_institutional_login),
                            style = MaterialTheme.typography.labelSmall,
                            color = ScholarlyTeal,
                        )
                    }
                }
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                )
            },
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password field with Forgot Password link on the right
        PasswordField(
            label = stringResource(Res.string.auth_password_label),
            value = state.password,
            onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
            isVisible = state.isPasswordVisible,
            onToggleVisibility = { onIntent(LoginIntent.TogglePasswordVisibility) },
            placeholder = "••••••••",
            trailingLabel = {
                TextButton(
                    onClick = { onIntent(LoginIntent.ForgotPasswordClicked) },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.auth_forgot_password),
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberDark,
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Keep session checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = state.keepSession,
                onCheckedChange = { onIntent(LoginIntent.ToggleKeepSession) },
            )
            Text(
                text = stringResource(Res.string.auth_keep_session),
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
            )
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

        // Login button
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
                    text = "Loading…",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
            } else {
                Text(
                    text = "${stringResource(Res.string.auth_btn_teacher)} →",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // SSO link
        TextButton(
            onClick = { onIntent(LoginIntent.InstitutionalSSOClicked) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.auth_sso_link),
                style = MaterialTheme.typography.bodySmall,
                color = ScholarlyTeal,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Faculty onboarding card
        FacultyOnboardingCard(
            onCallAdmissions = { onIntent(LoginIntent.CallAdmissionsClicked) },
            onWhatsApp = { onIntent(LoginIntent.WhatsAppFacultyClicked) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

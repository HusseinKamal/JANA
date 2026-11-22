package com.hussein.jana.feature.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.VellumCanvas
import com.hussein.jana.core.designsystem.error_email_invalid
import com.hussein.jana.core.designsystem.error_email_required
import com.hussein.jana.core.designsystem.error_name_required
import com.hussein.jana.core.designsystem.error_name_too_short
import com.hussein.jana.core.designsystem.error_password_no_symbol
import com.hussein.jana.core.designsystem.error_password_no_uppercase
import com.hussein.jana.core.designsystem.error_password_too_short
import com.hussein.jana.core.designsystem.error_passwords_mismatch
import com.hussein.jana.core.designsystem.error_phone_invalid
import com.hussein.jana.core.designsystem.error_phone_required
import com.hussein.jana.core.designsystem.error_platform_code_required
import com.hussein.jana.core.designsystem.error_terms_required
import com.hussein.jana.core.designsystem.loading
import com.hussein.jana.core.designsystem.register_btn
import com.hussein.jana.core.designsystem.register_have_account
import com.hussein.jana.core.designsystem.register_sign_in_link
import com.hussein.jana.core.designsystem.register_step_label
import com.hussein.jana.core.designsystem.register_step_progress
import com.hussein.jana.core.designsystem.register_terms_text
import com.hussein.jana.feature.auth.register.components.AccountSecurityCard
import com.hussein.jana.feature.auth.register.components.PersonalCredentialsCard
import com.hussein.jana.feature.auth.register.components.RegisterTopBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.text.style.TextOverflow

// ── Stateful entry point ───────────────────────────────────────────────────────

/**
 * Stateful entry point — acquires [RegisterViewModel] from Koin, collects effects,
 * and delegates navigation to the caller.
 */
@Composable
fun RegisterFlow(
    onNavigateToHome: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val viewModel: RegisterViewModel = koinViewModel()

    // Resolve all localized error strings here and inject into ViewModel
    val errNameRequired     = stringResource(Res.string.error_name_required)
    val errNameTooShort     = stringResource(Res.string.error_name_too_short)
    val errPhoneRequired    = stringResource(Res.string.error_phone_required)
    val errPhoneInvalid     = stringResource(Res.string.error_phone_invalid)
    val errPwTooShort       = stringResource(Res.string.error_password_too_short)
    val errPwNoUppercase    = stringResource(Res.string.error_password_no_uppercase)
    val errPwNoSymbol       = stringResource(Res.string.error_password_no_symbol)
    val errPwMismatch       = stringResource(Res.string.error_passwords_mismatch)
    val errTermsRequired    = stringResource(Res.string.error_terms_required)
    val errPlatformRequired = stringResource(Res.string.error_platform_code_required)
    val errEmailRequired    = stringResource(Res.string.error_email_required)
    val errEmailInvalid     = stringResource(Res.string.error_email_invalid)

    LaunchedEffect(
        errNameRequired, errNameTooShort, errPhoneRequired, errPhoneInvalid,
        errPwTooShort, errPwNoUppercase, errPwNoSymbol, errPwMismatch, errTermsRequired,
        errPlatformRequired, errEmailRequired, errEmailInvalid,
    ) {
        viewModel.updateStrings(
            RegisterStrings(
                errorNameRequired        = errNameRequired,
                errorNameTooShort        = errNameTooShort,
                errorPhoneRequired       = errPhoneRequired,
                errorPhoneInvalid        = errPhoneInvalid,
                errorPasswordTooShort    = errPwTooShort,
                errorPasswordNoUppercase = errPwNoUppercase,
                errorPasswordNoSymbol    = errPwNoSymbol,
                errorPasswordsMismatch   = errPwMismatch,
                errorTermsRequired       = errTermsRequired,
                errorPlatformCodeRequired = errPlatformRequired,
                errorEmailRequired       = errEmailRequired,
                errorEmailInvalid        = errEmailInvalid,
            ),
        )
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterEffect.NavigateToHome   -> onNavigateToHome()
                RegisterEffect.NavigateToSignIn -> onNavigateToSignIn()
            }
        }
    }

    RegisterScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
    )
}

// ── Stateless screen ───────────────────────────────────────────────────────────

/**
 * Stateless registration screen. Receives all state from above and fires intents upward.
 */
@Composable
fun RegisterScreen(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VellumCanvas),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Top bar ───────────────────────────────────────────────────────
            RegisterTopBar(onNavigateBack = onNavigateBack)

            // ── Content ───────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Step progress
                ProgressSection()

                // Card 1 — Personal Credentials
                PersonalCredentialsCard(
                    state = state,
                    onIntent = onIntent,
                )

                // Card 2 — Account Security
                AccountSecurityCard(
                    state = state,
                    onIntent = onIntent,
                )

                // Terms & Conditions checkbox
                TermsRow(state = state, onIntent = onIntent)

                // General error
                if (state.generalError != null) {
                    Text(
                        text = state.generalError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                // Primary CTA button
                RegisterButton(state = state, onIntent = onIntent)

                // Footer — already have an account
                FooterSignInLink(onIntent = onIntent)
            }
        }
    }
}

// ── Private sub-composables ────────────────────────────────────────────────────

@Composable
private fun ProgressSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Amber dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AmberDark, RoundedCornerShape(50)),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(Res.string.register_step_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberDark,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = stringResource(Res.string.register_step_progress),
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        // Full-width progress bar (3dp, 50% fill)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    color = ScholarlyTeal.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50),
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(3.dp)
                    .background(
                        color = ScholarlyTeal,
                        shape = RoundedCornerShape(50),
                    ),
            )
        }
    }
}

@Composable
private fun TermsRow(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = state.acceptedTerms,
                onCheckedChange = { onIntent(RegisterIntent.ToggleTerms) },
                colors = CheckboxDefaults.colors(
                    checkedColor = ScholarlyTeal,
                    uncheckedColor = OnSurfaceVariant,
                ),
            )
            Text(
                text = stringResource(Res.string.register_terms_text),
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
            )
        }
        if (state.termsError != null) {
            Text(
                text = state.termsError,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 12.dp),
            )
        }
    }
}

@Composable
private fun RegisterButton(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { onIntent(RegisterIntent.RegisterClicked) },
        modifier = modifier
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
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.register_btn),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun FooterSignInLink(
    onIntent: (RegisterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haveAccount = stringResource(Res.string.register_have_account)
    val signInLink  = stringResource(Res.string.register_sign_in_link)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text      = haveAccount,
            style     = MaterialTheme.typography.labelSmall,
            color     = OnSurfaceVariant,
            modifier  = Modifier.weight(1f),
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start, // Keeps text at the start edge
        )
        Spacer(modifier = Modifier.width(8.dp)) // Adds breathing room before ellipsizing
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color          = AmberDark,
                        fontWeight     = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                    ),
                ) { append(signInLink) }
            },
            style    = MaterialTheme.typography.labelSmall,
            modifier = Modifier.clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick           = { onIntent(RegisterIntent.SignInClicked) },
            ),
            maxLines = 1,
        )
    }
}

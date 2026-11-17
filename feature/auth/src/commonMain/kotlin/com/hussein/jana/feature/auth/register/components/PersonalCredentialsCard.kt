package com.hussein.jana.feature.auth.register.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.RecentActors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaCard
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.register_full_name_bilingual
import com.hussein.jana.core.designsystem.register_full_name_hint
import com.hussein.jana.core.designsystem.register_full_name_label
import com.hussein.jana.core.designsystem.register_personal_title
import com.hussein.jana.core.designsystem.register_phone_hint
import com.hussein.jana.core.designsystem.register_phone_label
import com.hussein.jana.feature.auth.components.AuthTextField
import com.hussein.jana.feature.auth.register.RegisterIntent
import com.hussein.jana.feature.auth.register.RegisterState
import org.jetbrains.compose.resources.stringResource

/**
 * "Personal Credentials" card — Full Legal Name + WhatsApp/Mobile Contact.
 * Name validation errors render inline; phone uses [PhoneField].
 */
@Composable
fun PersonalCredentialsCard(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    JanaCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {

            // ── Section header ────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.RecentActors,
                    contentDescription = null,
                    tint = ScholarlyTeal,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.register_personal_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = ScholarlyTeal,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // ── Full Legal Name field ─────────────────────────────────────────
            val nameLabel    = stringResource(Res.string.register_full_name_label)
            val bilingualTag = stringResource(Res.string.register_full_name_bilingual)
            AuthTextField(
                label = nameLabel,
                value = state.fullName,
                onValueChange = { onIntent(RegisterIntent.FullNameChanged(it)) },
                placeholder = stringResource(Res.string.register_full_name_hint),
                modifier = Modifier.fillMaxWidth(),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                    )
                },
                labelContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = nameLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = bilingualTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                        )
                    }
                },
            )

            if (state.fullNameError != null) {
                Text(
                    text = state.fullNameError,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── WhatsApp / Mobile Contact field ───────────────────────────────
            AuthTextField(
                label = stringResource(Res.string.register_phone_label),
                value = state.phone,
                onValueChange = { onIntent(RegisterIntent.PhoneChanged(it)) },
                placeholder = stringResource(Res.string.register_phone_hint),
                modifier = Modifier.fillMaxWidth(),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Phone,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                    )
                },
            )

            if (state.phoneError != null) {
                Text(
                    text = state.phoneError,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

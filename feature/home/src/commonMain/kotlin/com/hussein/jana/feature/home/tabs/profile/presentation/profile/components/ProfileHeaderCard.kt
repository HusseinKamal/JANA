package com.hussein.jana.feature.home.tabs.profile.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaOutlinedButton
import com.hussein.jana.core.designsystem.JanaProfileHeader
import com.hussein.jana.core.designsystem.JanaSettingsSection
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.profile_edit_profile
import com.hussein.jana.core.designsystem.profile_photo_cd
import org.jetbrains.compose.resources.stringResource

/** Student photo, name, email and phone in a bordered card, with an Edit profile action. */
@Composable
fun ProfileHeaderCard(
    name: String,
    email: String,
    phone: String,
    photoUrl: String?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    JanaSettingsSection(title = null, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            JanaProfileHeader(
                name = name,
                subtitle = email,
                bio = phone,
                imageUrl = photoUrl,
                avatarContentDescription = stringResource(Res.string.profile_photo_cd),
            )
            JanaOutlinedButton(
                text = stringResource(Res.string.profile_edit_profile),
                onClick = onEditClick,
            )
        }
    }
}

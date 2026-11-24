package com.hussein.jana.feature.home.tabs.profile.presentation.edit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaAvatar
import com.hussein.jana.core.designsystem.JanaButton
import com.hussein.jana.core.designsystem.JanaTextField
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.btn_save
import com.hussein.jana.core.designsystem.email_label
import com.hussein.jana.core.designsystem.name_label
import com.hussein.jana.core.designsystem.profile_change_photo
import com.hussein.jana.core.designsystem.profile_email_hint
import com.hussein.jana.core.designsystem.profile_name_hint
import com.hussein.jana.core.designsystem.profile_phone_hint
import com.hussein.jana.core.designsystem.profile_phone_label
import com.hussein.jana.core.designsystem.profile_photo_cd
import com.hussein.jana.feature.home.tabs.profile.domain.model.ProfileFieldError
import com.hussein.jana.feature.home.tabs.profile.presentation.common.message
import org.jetbrains.compose.resources.stringResource

/** Photo, name, email and phone fields with a Save button. All state is hoisted. */
@Composable
fun EditProfileForm(
    fullName: String,
    email: String,
    phone: String,
    photoUrl: String?,
    nameError: ProfileFieldError?,
    emailError: ProfileFieldError?,
    phoneError: ProfileFieldError?,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onChangePhotoClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            JanaAvatar(
                name = fullName,
                imageUrl = photoUrl,
                size = 96.dp,
                contentDescription = stringResource(Res.string.profile_photo_cd),
            )
            TextButton(onClick = onChangePhotoClick, enabled = !isSaving) {
                Text(text = stringResource(Res.string.profile_change_photo))
            }
        }

        JanaTextField(
            value = fullName,
            onValueChange = onNameChange,
            label = stringResource(Res.string.name_label),
            placeholder = stringResource(Res.string.profile_name_hint),
            errorText = nameError?.message(),
            enabled = !isSaving,
            leadingIcon = Icons.Rounded.Person,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        JanaTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(Res.string.email_label),
            placeholder = stringResource(Res.string.profile_email_hint),
            errorText = emailError?.message(),
            enabled = !isSaving,
            leadingIcon = Icons.Rounded.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        )
        JanaTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = stringResource(Res.string.profile_phone_label),
            placeholder = stringResource(Res.string.profile_phone_hint),
            errorText = phoneError?.message(),
            enabled = !isSaving,
            leadingIcon = Icons.Rounded.Phone,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
        )

        JanaButton(
            text = stringResource(Res.string.btn_save),
            onClick = onSaveClick,
            isLoading = isSaving,
        )
    }
}

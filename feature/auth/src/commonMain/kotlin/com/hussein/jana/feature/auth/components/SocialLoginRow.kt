package com.hussein.jana.feature.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.SlatePrimary
import com.hussein.jana.core.designsystem.auth_facebook
import com.hussein.jana.core.designsystem.auth_google
import com.hussein.jana.core.designsystem.ic_facebook
import com.hussein.jana.core.designsystem.ic_google
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Official brand colours */
private val GoogleBlue    = Color(0xFF4285F4)
private val FacebookBlue  = Color(0xFF1877F2)

@Composable
fun SocialLoginRow(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ── Google — white pill with multi-colour G icon ──────────────────────
        OutlinedButton(
            onClick = onGoogleClick,
            modifier = Modifier.weight(1f).height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, OutlineVariant),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor   = SlatePrimary,
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter           = painterResource(Res.drawable.ic_google),
                    contentDescription = stringResource(Res.string.auth_google),
                    modifier          = Modifier.size(20.dp),
                    tint              = Color.Unspecified,  // keep the multi-colour paths
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text  = stringResource(Res.string.auth_google),
                    style = MaterialTheme.typography.labelLarge,
                    color = SlatePrimary,
                )
            }
        }

        // ── Facebook — solid blue pill with white f icon ──────────────────────
        Button(
            onClick  = onFacebookClick,
            modifier = Modifier.weight(1f).height(50.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = FacebookBlue,
                contentColor   = Color.White,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter           = painterResource(Res.drawable.ic_facebook),
                    contentDescription = stringResource(Res.string.auth_facebook),
                    modifier          = Modifier.size(20.dp),
                    tint              = Color.White,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text  = stringResource(Res.string.auth_facebook),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
            }
        }
    }
}

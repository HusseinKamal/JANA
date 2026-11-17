package com.hussein.jana.feature.auth.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.Error
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.SuccessGreen
import com.hussein.jana.core.designsystem.register_password_medium
import com.hussein.jana.core.designsystem.register_password_requirements
import com.hussein.jana.core.designsystem.register_password_strong
import com.hussein.jana.core.designsystem.register_password_weak
import com.hussein.jana.feature.auth.register.PasswordStrength
import org.jetbrains.compose.resources.stringResource

/**
 * Displays a strength badge pill, requirements hint, and three-segment bar.
 * Renders nothing when [strength] is [PasswordStrength.NONE].
 */
@Composable
fun PasswordStrengthIndicator(
    strength: PasswordStrength,
    modifier: Modifier = Modifier,
) {
    if (strength == PasswordStrength.NONE) return

    val strengthColor: Color = when (strength) {
        PasswordStrength.WEAK   -> Error
        PasswordStrength.MEDIUM -> AmberGold
        PasswordStrength.STRONG -> SuccessGreen
        PasswordStrength.NONE   -> Color.Transparent
    }

    val strengthLabel: String = when (strength) {
        PasswordStrength.WEAK   -> stringResource(Res.string.register_password_weak)
        PasswordStrength.MEDIUM -> stringResource(Res.string.register_password_medium)
        PasswordStrength.STRONG -> stringResource(Res.string.register_password_strong)
        PasswordStrength.NONE   -> ""
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // ── Badge row: pill on left, requirements hint on right ───────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Surface(
                shape = CircleShape,
                color = strengthColor,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (strength == PasswordStrength.STRONG) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = strengthLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }

            Text(
                text = stringResource(Res.string.register_password_requirements),
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ── Three-segment strength bars ───────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val bar1Color = strengthColor
            val bar2Color = if (strength == PasswordStrength.MEDIUM || strength == PasswordStrength.STRONG) strengthColor else OutlineVariant
            val bar3Color = if (strength == PasswordStrength.STRONG) strengthColor else OutlineVariant

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(bar1Color, RoundedCornerShape(2.dp)),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(bar2Color, RoundedCornerShape(2.dp)),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(bar3Color, RoundedCornerShape(2.dp)),
            )
        }
    }
}

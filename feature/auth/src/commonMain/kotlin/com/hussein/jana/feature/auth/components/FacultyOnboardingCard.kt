package com.hussein.jana.feature.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SurfaceContainerLow
import com.hussein.jana.core.designsystem.auth_call_admissions
import com.hussein.jana.core.designsystem.auth_onboarding_desc
import com.hussein.jana.core.designsystem.auth_onboarding_title
import com.hussein.jana.core.designsystem.auth_onboarding_warning
import com.hussein.jana.core.designsystem.auth_whatsapp_faculty
import org.jetbrains.compose.resources.stringResource

@Composable
fun FacultyOnboardingCard(
    onCallAdmissions: () -> Unit,
    onWhatsApp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = SurfaceContainerLow,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎓", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = stringResource(Res.string.auth_onboarding_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = ScholarlyTeal,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.auth_onboarding_warning).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = AmberDark,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.auth_onboarding_desc),
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCallAdmissions,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ScholarlyTeal),
            ) {
                Text(
                    text = "📞 ${stringResource(Res.string.auth_call_admissions)}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onWhatsApp,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, OutlineVariant),
            ) {
                Text(
                    text = "💬 ${stringResource(Res.string.auth_whatsapp_faculty)}",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

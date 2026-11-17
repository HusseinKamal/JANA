package com.hussein.jana.feature.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.JanaLogoCard
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Outline
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SuccessGreen
import com.hussein.jana.core.designsystem.SurfaceContainerLow
import com.hussein.jana.core.designsystem.auth_cairo_node
import com.hussein.jana.core.designsystem.auth_faculty_studio
import com.hussein.jana.core.designsystem.auth_student_login_label
import com.hussein.jana.core.designsystem.auth_studio_online
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthTopBar(
    isTeacherTab: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isTeacherTab) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = OnSurfaceVariant,
                    )
                }
                JanaLogoCard(size = 28.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "JANA",
                        style = MaterialTheme.typography.titleMedium,
                        color = ScholarlyTeal,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(Res.string.auth_faculty_studio),
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberDark,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                        contentDescription = "Help",
                        tint = OnSurfaceVariant,
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "Profile",
                        tint = OnSurfaceVariant,
                    )
                }
            }
            Row(
                modifier = Modifier.padding(start = 56.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.size(8.dp)) {
                    Surface(shape = CircleShape, color = SuccessGreen, modifier = Modifier.size(8.dp)) {}
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(Res.string.auth_studio_online),
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreen,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = SurfaceContainerLow,
                    border = BorderStroke(1.dp, Outline),
                ) {
                    Text(
                        text = stringResource(Res.string.auth_cairo_node),
                        style = MaterialTheme.typography.labelSmall,
                        color = ScholarlyTeal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurfaceVariant,
                )
            }
            JanaLogoCard(size = 28.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "JANA",
                style = MaterialTheme.typography.titleMedium,
                color = ScholarlyTeal,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(Res.string.auth_student_login_label),
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                tint = OnSurfaceVariant,
            )
        }
    }
}

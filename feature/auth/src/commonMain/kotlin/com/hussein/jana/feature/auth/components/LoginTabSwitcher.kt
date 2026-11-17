package com.hussein.jana.feature.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.auth_tab_faculty
import com.hussein.jana.core.designsystem.auth_tab_student
import com.hussein.jana.core.designsystem.auth_tab_student_portal
import com.hussein.jana.core.designsystem.auth_tab_teacher
import com.hussein.jana.feature.auth.LoginTab
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginTabSwitcher(
    activeTab: LoginTab,
    onTabSelected: (LoginTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(50),
        border = BorderStroke(2.dp, OutlineVariant),
        modifier = modifier,
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            // Student tab
            val studentSelected = activeTab == LoginTab.STUDENT
            Surface(
                shape = RoundedCornerShape(50),
                color = if (studentSelected) ScholarlyTeal else Color.Transparent,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(LoginTab.STUDENT) },
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.School,
                        contentDescription = null,
                        tint = if (studentSelected) Color.White else OnSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (activeTab == LoginTab.TEACHER)
                            stringResource(Res.string.auth_tab_student_portal)
                        else
                            stringResource(Res.string.auth_tab_student),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (studentSelected) Color.White else OnSurfaceVariant,
                    )
                }
            }
            // Teacher tab
            val teacherSelected = activeTab == LoginTab.TEACHER
            Surface(
                shape = RoundedCornerShape(50),
                color = if (teacherSelected) ScholarlyTeal else Color.Transparent,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(LoginTab.TEACHER) },
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        tint = if (teacherSelected) Color.White else OnSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (activeTab == LoginTab.TEACHER)
                            stringResource(Res.string.auth_tab_faculty)
                        else
                            stringResource(Res.string.auth_tab_teacher),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (teacherSelected) Color.White else OnSurfaceVariant,
                    )
                }
            }
        }
    }
}

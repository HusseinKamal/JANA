package com.hussein.jana.feature.home.tabs.teachers.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.AmberLight
import com.hussein.jana.core.designsystem.AstralIndigo
import com.hussein.jana.core.designsystem.PrimaryContainer
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.SuccessGreen
import com.hussein.jana.feature.home.tabs.teachers.BadgeStyle
import com.hussein.jana.feature.home.tabs.teachers.TeacherBadgeData

/**
 * A compact chip that renders a [TeacherBadgeData] with color styling driven by [BadgeStyle].
 *
 * Colors per style:
 * - GREEN  → SuccessGreen tinted bg / SuccessGreen text
 * - TEAL   → PrimaryContainer tinted bg / ScholarlyTeal text
 * - INDIGO → AstralIndigo tinted bg / AstralIndigo text
 * - AMBER  → AmberLight bg / AmberDark text
 * - GOLD   → AmberGold tinted bg / AmberDark text
 */
@Composable
fun TeacherBadgeChip(
    badge: TeacherBadgeData,
    modifier: Modifier = Modifier,
) {
    val (bg, textColor) = badgeColors(badge.style)

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = bg,
    ) {
        Text(
            text = badge.label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

// ── Private helpers ───────────────────────────────────────────────────────────

private fun badgeColors(style: BadgeStyle): Pair<Color, Color> = when (style) {
    BadgeStyle.GREEN  -> SuccessGreen.copy(alpha = 0.12f) to SuccessGreen
    BadgeStyle.TEAL   -> PrimaryContainer.copy(alpha = 0.5f) to ScholarlyTeal
    BadgeStyle.INDIGO -> AstralIndigo.copy(alpha = 0.12f) to AstralIndigo
    BadgeStyle.AMBER  -> AmberLight to AmberDark
    BadgeStyle.GOLD   -> AmberGold.copy(alpha = 0.15f) to AmberDark
}

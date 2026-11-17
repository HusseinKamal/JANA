package com.hussein.jana.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource

/**
 * Shared stateless top app bar used across the JANA app.
 *
 * Layout (left → right):
 *  [Back?] [Logo 28dp] [spacer 8dp] [screenTitle]  [weight 1f]  [Credits?] [Avatar?] [actions]
 *
 * The outer Column carries [statusBarsPadding] so the content row sits
 * correctly below the system status bar in edge-to-edge mode.
 */
@Composable
fun JanaTopBar(
    screenTitle: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    credits: Int? = null,
    showAvatar: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(VellumCanvas),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Back button (optional)
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = OnSurfaceVariant,
                    )
                }
            }

            // Brand logo + screen title
            JanaLogoCard(size = 28.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = screenTitle,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ScholarlyTeal,
            )

            // Push right-side content to the end
            Spacer(modifier = Modifier.weight(1f))

            // Credits badge (optional)
            if (credits != null) {
                CreditsBadge(credits = credits)
                Spacer(modifier = Modifier.width(8.dp))
            }

            // User avatar (optional)
            if (showAvatar) {
                JanaInitialsAvatar(initials = "H", size = 36.dp)
            }

            // Extra actions slot
            actions()
        }

        HorizontalDivider(color = OutlineVariant, thickness = 1.dp)
    }
}

// ── Credits badge ─────────────────────────────────────────────────────────────

@Composable
private fun CreditsBadge(credits: Int) {
    Surface(
        shape = CircleShape,
        color = AmberLight,
        border = BorderStroke(1.dp, AmberGold),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Paid,
                contentDescription = null,
                tint = AmberDark,
                modifier = Modifier.size(12.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$credits ${stringResource(Res.string.home_credits_label)}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = AmberDark,
            )
        }
    }
}

// ── Shared initials avatar ────────────────────────────────────────────────────

/**
 * A circular avatar that displays up to two uppercase initials.
 * Reusable in top bars, session cards, and teacher rows.
 */
@Composable
fun JanaInitialsAvatar(
    initials: String,
    size: Dp = 36.dp,
    backgroundColor: Color = ScholarlyTeal,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials.take(2),
            style = MaterialTheme.typography.titleSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

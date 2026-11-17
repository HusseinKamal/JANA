package com.hussein.jana.feature.splash.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hussein.jana.core.designsystem.AmberDark
import com.hussein.jana.core.designsystem.AmberGold
import com.hussein.jana.core.designsystem.JanaLogoCard
import com.hussein.jana.core.designsystem.OnBackground
import com.hussein.jana.core.designsystem.OnSurfaceVariant
import com.hussein.jana.core.designsystem.Outline
import com.hussein.jana.core.designsystem.OutlineVariant
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.ScholarlyTeal
import com.hussein.jana.core.designsystem.VellumCanvas
import com.hussein.jana.core.designsystem.app_name
import com.hussein.jana.core.designsystem.app_name_arabic
import com.hussein.jana.core.designsystem.app_version
import com.hussein.jana.core.designsystem.btn_get_started
import com.hussein.jana.core.designsystem.splash_ai_chip
import com.hussein.jana.core.designsystem.splash_category
import com.hussein.jana.core.designsystem.splash_description
import com.hussein.jana.core.designsystem.splash_sign_in_link
import com.hussein.jana.core.designsystem.splash_sign_in_prompt
import com.hussein.jana.core.designsystem.splash_stats
import org.jetbrains.compose.resources.stringResource

@Composable
fun SplashContent(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VellumCanvas)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Top bar row ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoStories,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = ScholarlyTeal,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.splash_category),
                style = MaterialTheme.typography.labelMedium,
                color = ScholarlyTeal,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Outline,
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── JANA Logo Card ────────────────────────────────────────────────────
        JanaLogoCard(size = 120.dp)

        Spacer(modifier = Modifier.height(20.dp))

        // ── App name row ──────────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = ScholarlyTeal,
                letterSpacing = (-0.5).sp,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(Res.string.app_name_arabic),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = AmberGold,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── AI chip pill ──────────────────────────────────────────────────────
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(1.dp, OutlineVariant),
        ) {
            Text(
                text = stringResource(Res.string.splash_ai_chip),
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = ScholarlyTeal,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Description ───────────────────────────────────────────────────────
        Text(
            text = stringResource(Res.string.splash_description),
            modifier = Modifier.padding(horizontal = 36.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
        )

        Spacer(modifier = Modifier.height(18.dp))

        // ── Stats pill ────────────────────────────────────────────────────────
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(1.dp, OutlineVariant),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AmberGold),
                )
                Spacer(modifier = Modifier.width(9.dp))
                Text(
                    text = stringResource(Res.string.splash_stats),
                    style = MaterialTheme.typography.labelMedium,
                    color = OnBackground,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // ── Get Started button ────────────────────────────────────────────────
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = ScholarlyTeal,
                contentColor = Color.White,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                hoveredElevation = 0.dp,
                focusedElevation = 0.dp,
            ),
        ) {
            Text(
                text = stringResource(Res.string.btn_get_started),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ── Sign-in link row ──────────────────────────────────────────────────
        val signInPrompt = stringResource(Res.string.splash_sign_in_prompt)
        val signInLink = stringResource(Res.string.splash_sign_in_link)
        val annotatedText = buildAnnotatedString {
            withStyle(SpanStyle(color = OnSurfaceVariant)) {
                append(signInPrompt)
            }
            append(" ")
            withStyle(
                SpanStyle(
                    color = AmberDark,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                )
            ) {
                append(signInLink)
            }
        }
        Text(
            text = annotatedText,
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onSignIn,
            ),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── App version ───────────────────────────────────────────────────────
        Text(
            text = stringResource(Res.string.app_version),
            style = MaterialTheme.typography.labelSmall,
            color = Outline,
        )

        Spacer(modifier = Modifier.height(28.dp))
    }
}

package com.hussein.jana.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Scholarly Luminary typography.
 *
 * Headings use a Serif editorial style (Newsreader) and body/labels use
 * a geometric humanist sans (Plus Jakarta Sans).  The actual font files
 * (newsreader_*.ttf, plus_jakarta_sans_*.ttf) should be placed in:
 *   core/designsystem/src/commonMain/composeResources/font/
 * and wired via:
 *   Font(Res.font.newsreader_semibold, FontWeight.SemiBold)
 *
 * Until the files are added the system default serif / sans families
 * are used as runtime fallbacks — layout and sizing are correct.
 */

// Fallback families until custom font files are dropped into composeResources/font/
private val SerifFamily = FontFamily.Serif      // → Newsreader
private val SansFamily  = FontFamily.SansSerif  // → Plus Jakarta Sans

/**
 * Scale derived from DESIGN.md "Scholarly Luminary" specification:
 * - display/headline roles → Newsreader (editorial serif, weight 500–600)
 * - body/label roles       → Plus Jakarta Sans (humanist sans, weight 400–700)
 * - line-heights            → generous 1.5×–1.75× for bilingual study readability
 */
val JanaTypography = Typography(
    // ── Display ──────────────────────────────────────────────────────────────
    displayLarge  = TextStyle(
        fontFamily   = SerifFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 40.sp,
        lineHeight   = 48.sp,
        letterSpacing = (-0.02).sp,
    ),
    displayMedium = TextStyle(
        fontFamily   = SerifFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 32.sp,
        lineHeight   = 40.sp,
        letterSpacing = (-0.01).sp,
    ),
    displaySmall  = TextStyle(
        fontFamily   = SerifFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 28.sp,
        lineHeight   = 36.sp,
        letterSpacing = (-0.01).sp,
    ),
    // ── Headline ─────────────────────────────────────────────────────────────
    headlineLarge  = TextStyle(
        fontFamily = SerifFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = SerifFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 24.sp,
        lineHeight = 32.sp,
    ),
    headlineSmall  = TextStyle(
        fontFamily = SerifFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 20.sp,
        lineHeight = 28.sp,
    ),
    // ── Title ────────────────────────────────────────────────────────────────
    titleLarge   = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        lineHeight = 28.sp,
    ),
    titleMedium  = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
    ),
    titleSmall   = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
    ),
    // ── Body ─────────────────────────────────────────────────────────────────
    bodyLarge    = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 18.sp,
        lineHeight = 28.sp,
    ),
    bodyMedium   = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
    ),
    bodySmall    = TextStyle(
        fontFamily = SansFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
    ),
    // ── Label ────────────────────────────────────────────────────────────────
    labelLarge   = TextStyle(
        fontFamily    = SansFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.01.sp,
    ),
    labelMedium  = TextStyle(
        fontFamily    = SansFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.02.sp,
    ),
    labelSmall   = TextStyle(
        fontFamily    = SansFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 10.sp,
        lineHeight    = 14.sp,
        letterSpacing = 0.04.sp,
    ),
)

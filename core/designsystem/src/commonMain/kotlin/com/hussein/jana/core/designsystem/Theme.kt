package com.hussein.jana.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ── Light colour scheme — Scholarly Luminary ─────────────────────────────────
val JanaLightColors = lightColorScheme(
    primary              = Primary,
    onPrimary            = OnPrimary,
    primaryContainer     = PrimaryContainer,
    onPrimaryContainer   = OnPrimaryContainer,
    inversePrimary       = InversePrimary,
    secondary            = Secondary,
    onSecondary          = OnSecondary,
    secondaryContainer   = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary             = Tertiary,
    onTertiary           = OnTertiary,
    tertiaryContainer    = TertiaryContainer,
    onTertiaryContainer  = OnTertiaryContainer,
    error                = Error,
    onError              = OnError,
    errorContainer       = ErrorContainer,
    onErrorContainer     = OnErrorContainer,
    background           = Background,
    onBackground         = OnBackground,
    surface              = Surface,
    onSurface            = OnSurface,
    surfaceVariant       = SurfaceVariant,
    onSurfaceVariant     = OnSurfaceVariant,
    surfaceTint          = SurfaceTint,
    inverseSurface       = InverseSurface,
    inverseOnSurface     = InverseOnSurface,
    outline              = Outline,
    outlineVariant       = OutlineVariant,
    scrim                = androidx.compose.ui.graphics.Color(0x33000000),
)

// ── Dark colour scheme ────────────────────────────────────────────────────────
val JanaDarkColors = darkColorScheme(
    primary              = PrimaryFixed,          // #BDECE0 — mint glow
    onPrimary            = OnPrimaryFixed,         // #00201B
    primaryContainer     = PrimaryContainer,       // #0F3E36
    onPrimaryContainer   = PrimaryFixedDim,        // #A2D0C4
    inversePrimary       = Primary,
    secondary            = SecondaryFixedDim,      // #FFB77D — warm amber
    onSecondary          = OnSecondaryFixed,       // #2F1500
    secondaryContainer   = Secondary,              // #904D00 deep amber
    onSecondaryContainer = SecondaryFixed,         // #FFDCC3
    tertiary             = TertiaryFixedDim,       // #C0C1FF
    onTertiary           = OnTertiaryFixed,        // #07006C
    tertiaryContainer    = TertiaryContainer,      // #1A13B0
    onTertiaryContainer  = TertiaryFixed,          // #E1E0FF
    error                = Error,
    onError              = OnError,
    background           = DarkBackground,
    onBackground         = DarkOnSurface,
    surface              = DarkSurface,
    onSurface            = DarkOnSurface,
    surfaceVariant       = DarkSurfaceVar,
    onSurfaceVariant     = DarkOutline,
    surfaceTint          = TealGlow,
    inverseSurface       = DarkOnSurface,
    inverseOnSurface     = DarkBackground,
    outline              = DarkOutline,
    outlineVariant       = DarkOutlineVar,
)

@Composable
fun JanaTheme(
    darkTheme: Boolean = LocalIsDarkMode.current.value,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) JanaDarkColors else JanaLightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = JanaTypography,
        content     = content,
    )
}

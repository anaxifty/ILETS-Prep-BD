package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// =====================================================================
// Lumen native design tokens — zero Material3 dependency.
// The Emerald Focus palettes expressed as plain, app-owned token sets,
// provided through CompositionLocals instead of MaterialTheme.
// =====================================================================

@Immutable data class LumenColors(
  val primary: Color,
  val onPrimary: Color,
  val primaryContainer: Color,
  val onPrimaryContainer: Color,
  val inversePrimary: Color,
  val secondary: Color,
  val onSecondary: Color,
  val secondaryContainer: Color,
  val onSecondaryContainer: Color,
  val tertiary: Color,
  val onTertiary: Color,
  val tertiaryContainer: Color,
  val onTertiaryContainer: Color,
  val error: Color,
  val onError: Color,
  val errorContainer: Color,
  val onErrorContainer: Color,
  val background: Color,
  val onBackground: Color,
  val surface: Color,
  val onSurface: Color,
  val surfaceVariant: Color,
  val onSurfaceVariant: Color,
  val surfaceContainerLowest: Color,
  val surfaceContainerLow: Color,
  val surfaceContainer: Color,
  val surfaceContainerHigh: Color,
  val surfaceContainerHighest: Color,
  val outline: Color,
  val outlineVariant: Color,
  val inverseSurface: Color,
  val inverseOnSurface: Color,
  val scrim: Color,
)

val LightLumenColors =
  LumenColors(
    primary = EmeraldDeep,
    onPrimary = Color.White,
    primaryContainer = EmeraldMint,
    onPrimaryContainer = EmeraldInk,
    inversePrimary = EmeraldBright,
    secondary = EmeraldMid,
    onSecondary = Color.White,
    secondaryContainer = EmeraldMint,
    onSecondaryContainer = EmeraldInk,
    tertiary = AmberGold,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = AmberOnContainer,
    error = DangerRed,
    onError = Color.White,
    errorContainer = DangerContainer,
    onErrorContainer = DangerOnContainer,
    background = PaperBgLight,
    onBackground = InkTextLight,
    surface = PaperSurfaceLight,
    onSurface = InkTextLight,
    surfaceVariant = PaperCardLight,
    onSurfaceVariant = InkMutedLight,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = PaperBgLight,
    surfaceContainer = PaperCardLight,
    surfaceContainerHigh = Color(0xFFE8EFE9),
    surfaceContainerHighest = Color(0xFFDFE8E2),
    outline = HairlineLight,
    outlineVariant = Color(0xFFDFE7E0),
    inverseSurface = Color(0xFF2B332E),
    inverseOnSurface = Color(0xFFECF2EC),
    scrim = Color(0xFF00120B),
  )

val DarkLumenColors =
  LumenColors(
    primary = EmeraldBright,
    onPrimary = Color(0xFF003826),
    primaryContainer = Color(0xFF00513A),
    onPrimaryContainer = EmeraldMint,
    inversePrimary = EmeraldDeep,
    secondary = Color(0xFF8ED6B4),
    onSecondary = Color(0xFF003826),
    secondaryContainer = Color(0xFF1A4A38),
    onSecondaryContainer = Color(0xFFBFF1D8),
    tertiary = Color(0xFFF2B968),
    onTertiary = Color(0xFF3A2500),
    tertiaryContainer = Color(0xFF7A4900),
    onTertiaryContainer = AmberContainer,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = NightBgDark,
    onBackground = NightTextDark,
    surface = NightSurfaceDark,
    onSurface = NightTextDark,
    surfaceVariant = NightVariantDark,
    onSurfaceVariant = NightMutedDark,
    surfaceContainerLowest = Color(0xFF080D0B),
    surfaceContainerLow = Color(0xFF111915),
    surfaceContainer = NightCardDark,
    surfaceContainerHigh = Color(0xFF232E28),
    surfaceContainerHighest = Color(0xFF2C3831),
    outline = HairlineDark,
    outlineVariant = Color(0xFF2C3831),
    inverseSurface = NightTextDark,
    inverseOnSurface = Color(0xFF1B231E),
    scrim = Color(0xFF000000),
  )

/** The 13-level Emerald Focus type scale (see Type.kt for definitions). */
@Immutable
data class LumenTypography(
  val displayLarge: TextStyle = LumenDisplayLarge,
  val displayMedium: TextStyle = LumenDisplayMedium,
  val displaySmall: TextStyle = LumenDisplaySmall,
  val headlineLarge: TextStyle = LumenHeadlineLarge,
  val headlineMedium: TextStyle = LumenHeadlineMedium,
  val headlineSmall: TextStyle = LumenHeadlineSmall,
  val titleLarge: TextStyle = LumenTitleLarge,
  val titleMedium: TextStyle = LumenTitleMedium,
  val titleSmall: TextStyle = LumenTitleSmall,
  val bodyLarge: TextStyle = LumenBodyLarge,
  val bodyMedium: TextStyle = LumenBodyMedium,
  val bodySmall: TextStyle = LumenBodySmall,
  val labelLarge: TextStyle = LumenLabelLarge,
  val labelMedium: TextStyle = LumenLabelMedium,
  val labelSmall: TextStyle = LumenLabelSmall,
)

/** Unified corner-radius scale, owned by the app (no MD3 Shapes). */
@Immutable
data class LumenShapes(
  val extraSmall: Shape = RoundedCornerShape(6.dp),
  val small: Shape = RoundedCornerShape(10.dp),
  val medium: Shape = RoundedCornerShape(14.dp),
  val large: Shape = RoundedCornerShape(20.dp),
  val extraLarge: Shape = RoundedCornerShape(26.dp),
  val hero: Shape = HeroCardShape,
  val jumbo: Shape = JumboShape,
  val chip: Shape = ChipShape,
)

val LocalLumenColors = staticCompositionLocalOf { LightLumenColors }
val LocalLumenTypography: ProvidableCompositionLocal<LumenTypography> =
  staticCompositionLocalOf { LumenTypography() }
val LocalLumenShapes: ProvidableCompositionLocal<LumenShapes> =
  staticCompositionLocalOf { LumenShapes() }

/** Native accessor replacing `MaterialTheme` — e.g. `LumenTheme.colors.primary`. */
object LumenTheme {
  val colors: LumenColors
    @Composable get() = LocalLumenColors.current
  val typography: LumenTypography
    @Composable get() = LocalLumenTypography.current
  val shapes: LumenShapes
    @Composable get() = LocalLumenShapes.current
}

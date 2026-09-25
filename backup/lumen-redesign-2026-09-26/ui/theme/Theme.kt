package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// "Emerald Focus" — full Material 3 role coverage.
private val LightColorScheme =
  lightColorScheme(
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

private val DarkColorScheme =
  darkColorScheme(
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

// Unified corner-radius scale (replaces the old scattered 8/12/14/16/18/20/22/24/28 mix).
val IELTSShapes =
  Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp),
  )

// Extra tokens screens can reference for hero cards / bottom sheets.
val HeroCardShape = RoundedCornerShape(26.dp)
val JumboShape = RoundedCornerShape(32.dp)
val ChipShape = RoundedCornerShape(10.dp)

@Composable
fun IELTSPrepTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve the Emerald Focus brand identity.
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      WindowCompat.getInsetsController(window, view)
        .isAppearanceLightStatusBars = !darkTheme && colorScheme.surface.luminance() > 0.5f
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = IELTSShapes,
    content = content,
  )
}

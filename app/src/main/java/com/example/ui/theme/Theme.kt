package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.ui.components.LocalLumenContentColor

// =====================================================================
// Lumen "Emerald Focus" theme root — native (zero Material3 imports).
// Tokens flow through CompositionLocals defined in LumenTokens.kt.
// =====================================================================

// Unified corner-radius scale (replaces the old scattered 8/12/14/16/18/20/22/24/28 mix).
val HeroCardShape = RoundedCornerShape(26.dp)
val JumboShape = RoundedCornerShape(32.dp)
val ChipShape = RoundedCornerShape(10.dp)

@Composable
fun IELTSPrepTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve the Emerald Focus brand identity.
  content: @Composable () -> Unit,
) {
  val colors = if (darkTheme) DarkLumenColors else LightLumenColors

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      WindowCompat.getInsetsController(window, view)
        .isAppearanceLightStatusBars = !darkTheme && colors.surface.luminance() > 0.5f
    }
  }

  CompositionLocalProvider(
    LocalLumenColors provides colors,
    LocalLumenTypography provides LumenTypography(),
    LocalLumenShapes provides LumenShapes(),
    LocalLumenContentColor provides colors.onBackground,
    content = content,
  )
}

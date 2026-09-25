package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
  lightColorScheme(
    primary = GeometricPrimaryLight,
    onPrimary = GeometricOnPrimaryLight,
    primaryContainer = GeometricPrimaryContainerLight,
    onPrimaryContainer = GeometricOnPrimaryContainerLight,
    secondary = GeometricSecondaryLight,
    secondaryContainer = GeometricSecondaryContainerLight,
    onSecondaryContainer = GeometricOnSecondaryContainerLight,
    tertiary = GeometricTertiaryLight,
    tertiaryContainer = GeometricTertiaryContainerLight,
    onTertiaryContainer = GeometricOnTertiaryContainerLight,
    background = GeometricBgLight,
    onBackground = GeometricOnSurfaceLight,
    surface = GeometricSurfaceLight,
    onSurface = GeometricOnSurfaceLight,
    surfaceVariant = GeometricSurfaceVariantLight,
    onSurfaceVariant = GeometricOnSurfaceMutedLight,
    outline = GeometricOutlineLight
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = GeometricPrimaryDark,
    onPrimary = GeometricOnPrimaryDark,
    primaryContainer = GeometricPrimaryContainerDark,
    onPrimaryContainer = GeometricOnPrimaryContainerDark,
    secondaryContainer = GeometricSecondaryContainerDark,
    onSecondaryContainer = GeometricOnSecondaryContainerDark,
    background = GeometricBgDark,
    onBackground = GeometricOnSurfaceDark,
    surface = GeometricSurfaceDark,
    onSurface = GeometricOnSurfaceDark,
    surfaceVariant = GeometricSurfaceVariantDark,
    onSurfaceVariant = GeometricOnSurfaceMutedDark,
    outline = GeometricOutlineDark
  )

@Composable
fun IELTSPrepTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set false to preserve our custom Geometric Balance palette identity
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

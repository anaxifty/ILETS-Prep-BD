package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =====================================================================
// "Emerald Focus" — IELTS Prep BD redesign palette (2026)
// Psychology: green = growth/competence/calm (lowers test anxiety),
// amber gold = reward/motivation accent, red reserved for real errors.
// Culturally resonant with Bangladesh's national green + red circle.
// =====================================================================

// ---- Brand core -----------------------------------------------------
val EmeraldDeep = Color(0xFF00543C) // primary light — verdant, trustworthy
val EmeraldMid = Color(0xFF00785A) // pressed / emphasis
val EmeraldBright = Color(0xFF6FD6AC) // primary on dark surfaces
val EmeraldMint = Color(0xFFBFF1D8) // primary container light
val EmeraldInk = Color(0xFF002015) // on primary container (dark ink)

val AmberGold = Color(0xFFB26A00) // tertiary — achievement / streaks
val AmberContainer = Color(0xFFFFDFBD)
val AmberOnContainer = Color(0xFF3A2500)

// ---- Neutrals (mint-tinted paper) ------------------------------------
val PaperBgLight = Color(0xFFF5FAF7)
val PaperSurfaceLight = Color(0xFFFBFDFB)
val PaperCardLight = Color(0xFFF0F5F1)
val PaperPassageLight = Color(0xFFFCFEFD) // reading "exam paper" surface
val InkTextLight = Color(0xFF161D1A)
val InkMutedLight = Color(0xFF45524B)
val HairlineLight = Color(0xFFCBD7CF)

val NightBgDark = Color(0xFF0E1512)
val NightSurfaceDark = Color(0xFF151D19)
val NightCardDark = Color(0xFF1D2722)
val NightVariantDark = Color(0xFF2A3630)
val NightTextDark = Color(0xFFE1EADF)
val NightMutedDark = Color(0xFF8FA096)
val HairlineDark = Color(0xFF3C4A43)

// ---- Semantic status colors (unified across screens) -----------------
val SuccessGreen = Color(0xFF1B8A5A)
val SuccessContainer = Color(0xFFCDF4E0)
val SuccessOnContainer = Color(0xFF00341D)
val DangerRed = Color(0xFFBA1A1A)
val DangerContainer = Color(0xFFFFDAD6)
val DangerOnContainer = Color(0xFF410002)
val InfoBlue = Color(0xFF00658F)
val InfoContainer = Color(0xFFC5E7FF)
val WarnAmber = Color(0xFF8A5A00)
val WarnContainer = Color(0xFFFFE5B0)

// Legacy aliases kept so any straggling reference still compiles.
val GeometricPrimaryLight = EmeraldDeep
val GeometricSecondaryLight = EmeraldDeep
val GeometricTertiaryLight = AmberGold

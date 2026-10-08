package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = PrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = SkyBlueLight,
  onPrimaryContainer = PrimaryBlueDark,
  secondary = SecondaryOrange,
  onSecondary = Color.White,
  secondaryContainer = CardPastelOrange,
  onSecondaryContainer = Color(0xFFE65100),
  tertiary = TertiaryPink,
  onTertiary = Color.White,
  tertiaryContainer = CardPastelPink,
  onTertiaryContainer = Color(0xFF880E4F),
  background = Color(0xFFF8FAFC),
  onBackground = Color(0xFF1E293B),
  surface = Color.White,
  onSurface = Color(0xFF1E293B),
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF475569)
)

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = Color(0xFF0D47A1),
  onPrimaryContainer = Color.White,
  secondary = SecondaryOrange,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFFE65100),
  onSecondaryContainer = Color.White,
  tertiary = TertiaryPink,
  onTertiary = Color.White,
  background = DarkBackground,
  onBackground = Color(0xFFF1F5F9),
  surface = DarkSurface,
  onSurface = Color(0xFFF1F5F9),
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep cheerful custom colors consistent
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

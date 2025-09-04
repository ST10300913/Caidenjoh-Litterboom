package com.example.litterboom.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DarkJungleGreen, // Main interactive color (buttons etc.)
    secondary = LightTeal,    // Secondary accent color
    background = White,       // App background
    surface = White,          // Surface color for cards, sheets
    onPrimary = White,        // Text color on top of a primary color button
    onSecondary = Black,      // Text color on top of secondary color
    onBackground = Black,     // Main text color on a background
    onSurface = Black         // Main text color on surfaces
)

@Composable
fun LitterboomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}


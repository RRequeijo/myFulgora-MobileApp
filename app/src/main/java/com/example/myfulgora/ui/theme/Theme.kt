package com.example.myfulgora.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

// BALDE DA NOITE
private val DarkColorPalette = darkColorScheme(
    primary = GreenFresh,
    secondary = GreenDeep,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkInputBg,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    error = RedError
)

// BALDE DO DIA
private val LightColorPalette = lightColorScheme(
    primary = GreenFresh,
    secondary = GreenDeep,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightInputBg,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    error = RedError
)

@Composable
fun MyFulgoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color desligado para FORÇAR as cores da tua marca
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // 1. A LÓGICA DO CAMALEÃO (Agora sim, obedece ao 'darkTheme')
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorPalette // 👈 De noite, usa os baldes escuros
        else -> LightColorPalette     // 👈 De dia, usa os baldes claros
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 2. Pinta a barra do topo com a cor de fundo do tema atual
            window.statusBarColor = colorScheme.background.toArgb()

            // 3. ÍCONES INTELIGENTES: Ícones brancos no modo escuro, e pretos no modo claro!
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
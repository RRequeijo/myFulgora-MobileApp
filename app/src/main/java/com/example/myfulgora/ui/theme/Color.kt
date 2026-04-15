package com.example.myfulgora.ui.theme

import androidx.compose.ui.graphics.Color


// --- 1. CORES DA MARCA (Nunca mudam, seja dia ou noite) ---
val GreenFresh = Color(0xFF00D84F)   // "Fresh electric green" - A cor principal vibrante
val GreenDeep = Color(0xFF02855B)    // "Deep emerald green" - Para fundos e gradientes
val RedError = Color(0xFFFF5252)     // Alertas e erros
val White = Color(0xFFFFFFFF)         // Texto branco

// Backward compatibility aliases
val BlackBrand = Color(0xFF000000)
val CardBackgroundColor = Color(0xFF1E1E1E)

// --- 2. PALETA DO MODO ESCURO (A tua app atual) ---
val DarkBackground = Color(0xFF000000)       // O teu BlackBrand original
val DarkSurface = Color(0xFF1E1E1E)          // O teu CardBackgroundColor
val DarkInputBg = Color(0xFF222222)          // O teu DarkInputBackground
val DarkTextPrimary = Color(0xFFFFFFFF)      // O teu White
val DarkTextSecondary = Color(0xFFB0BEC5)    // O teu GrayLight

// --- 3. PALETA DO MODO CLARO (Ajustada: Fundo original + Cards Off-white) ---
val LightBackground = DarkBackground          // Mantemos o fundo original (preto)
val LightSurface = Color(0xFFF9FAFB)         // Branco "sujo" (mais suave e moderno)
val LightInputBg = Color(0xFFF9FAFB)         // Fundo dos inputs um pouco mais escuro que o card
val LightTextPrimary = Color(0xFF0F172A)     // Texto quase preto para leitura perfeita no card
val LightTextSecondary = Color(0xFF475569)   // Texto secundário cinza escuro
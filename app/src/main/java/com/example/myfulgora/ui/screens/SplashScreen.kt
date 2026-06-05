package com.example.myfulgora.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.myfulgora.R
import com.example.myfulgora.ui.theme.GreenFresh // A tua cor verde

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    // Temporizador de 2 segundos (Simulação de carregamento)
    LaunchedEffect(key1 = true) {
        delay(2000)
        onSplashFinished()
    }

    // O Design
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val logoRes = if (isSystemInDarkTheme()) R.drawable.logo_crop else R.drawable.logo_crop_2

            // O Logótipo
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp) // Ajusta o tamanho se necessário
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 👇 A BOLINHA DE CARREGAMENTO (Spinner)
            CircularProgressIndicator(
                color = GreenFresh, // Verde elétrico
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
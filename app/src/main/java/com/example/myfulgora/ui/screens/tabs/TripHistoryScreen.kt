package com.example.myfulgora.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myfulgora.ui.components.RecentTripRow // Verifica se o import está correto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripHistoryScreen(
    onBackClick: () -> Unit // Função para voltar ao mapa
) {
    Scaffold(
        containerColor = Color(0xFF1A1A1A), // Fundo escuro a combinar com a app
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Viagens", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            )
        }
    ) { paddingValues ->
        // LazyColumn é o ideal para listas grandes, pois só carrega o que está visível no ecrã!
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Aqui estamos a gerar 15 viagens "falsas" para testares o scroll
            items(15) { index ->
                RecentTripRow(
                    date = "Out ${15 - index}, 14:00",
                    route = if (index % 2 == 0) "Home - Office" else "Office - Gym",
                    distance = "${12 + index} km",
                    energy = "0.${4 + index} kWh"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Gray.copy(alpha = 0.1f)
                )
            }
        }
    }
}
package com.example.myfulgora.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.data.helpers.SettingsManager
import com.example.myfulgora.data.helpers.UnitConverter
import com.example.myfulgora.ui.components.RecentTripRow
import com.example.myfulgora.ui.theme.GreenFresh // Garante que este import está correto para a tua cor

// 1. Criamos um "Molde" para a Viagem, para ser mais fácil de filtrar
data class MockTrip(
    val id: Int,
    val date: String,
    val route: String,
    val distanceKm: Int,
    val energy: String,
    val category: String // "Hoje", "Semana" ou "Mês"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripHistoryScreen(
    onBackClick: () -> Unit // Função para voltar ao mapa
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val isMetric by settingsManager.isMetricFlow.collectAsState(initial = true)

    // 2. A variável que guarda qual o botão que está verde (O Filtro Atual)
    var selectedFilter by remember { mutableStateOf("Tudo") }

    // 3. Criamos a nossa lista "falsa" mas agora guardada na memória!
    val allTrips = remember {
        List(15) { index ->
            // Vamos simular que o 1º é "Hoje", os próximos 6 são "Semana" e o resto é "Mês"
            val category = when (index) {
                0 -> "Hoje"
                in 1..6 -> "Semana"
                else -> "Mês"
            }
            MockTrip(
                id = index,
                date = "Out ${15 - index}, 14:00",
                route = if (index % 2 == 0) "Home - Office" else "Office - Gym",
                distanceKm = 12 + index,
                energy = "0.${4 + index} kWh",
                category = category
            )
        }
    }

    // 4. A MAGIA DO FILTRO! Esta lista muda automaticamente sempre que clicas num botão
    val filteredTrips = remember(selectedFilter, allTrips) {
        when (selectedFilter) {
            "Hoje" -> allTrips.filter { it.category == "Hoje" }
            "Semana" -> allTrips.filter { it.category == "Hoje" || it.category == "Semana" }
            "Mês" -> allTrips.filter { it.category == "Hoje" || it.category == "Semana" || it.category == "Mês" }
            else -> allTrips // "Tudo" mostra a lista completa
        }
    }

    Scaffold(
        containerColor = Color(0xFF1A1A1A),
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // A BARRA DE FILTROS
            TripFilterBar(
                selectedFilter = selectedFilter,
                onFilterSelected = { novoFiltro -> selectedFilter = novoFiltro }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // A LISTA FILTRADA
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Em vez de "items(15)", agora passamos a nossa lista inteligente!
                items(filteredTrips) { trip ->
                    RecentTripRow(
                        date = trip.date,
                        route = trip.route,
                        distance = UnitConverter.formatDistance(trip.distanceKm, isMetric),
                        energy = trip.energy
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 8.dp),
                        color = Color.Gray.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

// O Componente da Barra de Filtros (Podes deixar aqui no mesmo ficheiro)
@Composable
fun TripFilterBar(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Hoje", "Semana", "Mês", "Tudo")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = selectedFilter == filter

            Surface(
                modifier = Modifier.weight(1f).height(32.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) GreenFresh else Color(0xFF2C2C2C),
                onClick = { onFilterSelected(filter) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
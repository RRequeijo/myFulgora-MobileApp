package com.example.myfulgora.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.example.myfulgora.ui.theme.GreenFresh
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// 1. O Molde (Adicionei uma data real para conseguirmos filtrar a sério no futuro)
data class MockTrip(
    val id: Int,
    val dateText: String,
    val route: String,
    val distanceKm: Int,
    val energy: String,
    val mockCategory: String // Apenas para simular enquanto não tens a API
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripHistoryScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val isMetric by settingsManager.isMetricFlow.collectAsState(initial = true)

    // ESTADOS DO FILTRO E TEMPO
    var selectedFilter by remember { mutableStateOf("Tudo") }
    var timeOffset by remember { mutableIntStateOf(0) } // 0 = Presente, -1 = Passado, etc.

    // A nossa lista simulada
    val allTrips = remember {
        List(15) { index ->
            val category = when (index) {
                0 -> "Dia"
                in 1..6 -> "Semana"
                else -> "Mês"
            }
            MockTrip(
                id = index,
                dateText = "Out ${15 - index}, 14:00",
                route = if (index % 2 == 0) "Home - Office" else "Office - Gym",
                distanceKm = 12 + index,
                energy = "0.${4 + index} kWh",
                mockCategory = category
            )
        }
    }

    // A MÁGICA DO FILTRO (Aqui, mais tarde, ligas as datas reais da API usando o timeOffset)
    val filteredTrips = remember(selectedFilter, timeOffset, allTrips) {
        // Por agora, para simular, se andares para trás no tempo, mostramos a lista vazia ou misturada
        when (selectedFilter) {
            "Dia" -> allTrips.filter { it.mockCategory == "Dia" && timeOffset == 0 }
            "Semana" -> allTrips.filter { (it.mockCategory == "Dia" || it.mockCategory == "Semana") && timeOffset == 0 }
            "Mês" -> allTrips.filter { timeOffset == 0 } // Simula que só há viagens este mês
            else -> allTrips
        }
    }

    Scaffold(
        containerColor = Color(0xFF1A1A1A),
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Viagens", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A1A))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 1. A BARRA DE BOTÕES (Dia, Semana, Mês, Tudo)
            TripFilterBar(
                selectedFilter = selectedFilter,
                onFilterSelected = { novoFiltro ->
                    selectedFilter = novoFiltro
                    timeOffset = 0 // Faz reset ao tempo sempre que mudas de filtro!
                }
            )

            // 2. O NAVEGADOR DE TEMPO (Só aparece se não for "Tudo")
            if (selectedFilter != "Tudo") {
                TimeNavigator(
                    filterType = selectedFilter,
                    offset = timeOffset,
                    onOffsetChange = { timeOffset = it }
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. A LISTA FILTRADA
            if (filteredTrips.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sem viagens neste período.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTrips) { trip ->
                        RecentTripRow(
                            date = trip.dateText,
                            route = trip.route,
                            distance = UnitConverter.formatDistance(trip.distanceKm, isMetric),
                            energy = trip.energy
                        )
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.Gray.copy(alpha = 0.1f))
                    }
                }
            }
        }
    }
}

// O NOVO COMPONENTE: AS SETINHAS DE NAVEGAÇÃO NO TEMPO ⏳
@Composable
fun TimeNavigator(
    filterType: String,
    offset: Int,
    onOffsetChange: (Int) -> Unit
) {
    // Calculamos o texto com base no filtro e no offset (usando as datas reais do telemóvel!)
    val labelText = remember(filterType, offset) {
        val today = LocalDate.now()
        when (filterType) {
            "Dia" -> {
                val targetDate = today.plusDays(offset.toLong())
                if (offset == 0) "Hoje"
                else if (offset == -1) "Ontem"
                else targetDate.format(DateTimeFormatter.ofPattern("dd MMM"))
            }
            "Semana" -> {
                if (offset == 0) "Esta Semana"
                else if (offset == -1) "Semana Passada"
                else "${Math.abs(offset)} semanas atrás"
            }
            "Mês" -> {
                val targetMonth = today.plusMonths(offset.toLong())
                if (offset == 0) "Este Mês"
                else targetMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() } + " " + targetMonth.year
            }
            else -> ""
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onOffsetChange(offset - 1) }) {
            Icon(Icons.Default.KeyboardArrowLeft, "Anterior", tint = GreenFresh)
        }

        Text(
            text = labelText,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        // Só deixa andar para a frente se não estivermos no presente (offset < 0)
        IconButton(
            onClick = { onOffsetChange(offset + 1) },
            enabled = offset < 0
        ) {
            Icon(
                Icons.Default.KeyboardArrowRight,
                "Seguinte",
                tint = if (offset < 0) GreenFresh else Color.Gray.copy(alpha = 0.3f)
            )
        }
    }
}

// O Componente da Barra de Filtros (Mudámos "Hoje" para "Dia")
@Composable
fun TripFilterBar(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Dia", "Semana", "Mês", "Tudo")

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
package com.example.myfulgora.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.myfulgora.R
import com.example.myfulgora.data.helpers.SettingsManager
import com.example.myfulgora.data.helpers.UnitConverter
import com.example.myfulgora.ui.components.RecentTripRow
import com.example.myfulgora.ui.theme.AppIcons
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// 1. O Molde da Viagem
data class Trip(
    val id: Int,
    val dateText: String,
    val route: String,
    val distanceKm: Int,
    val energy: String,
    val date: LocalDate
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
    var selectedFilter by remember { mutableStateOf("All") }
    var timeOffset by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Lista de viagens (Vazia por agora)
    val trips = remember { mutableStateListOf<Trip>() }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        
                        selectedFilter = "Day"
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.trip_history_confirm), color = MaterialTheme.colorScheme.primary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    headlineContentColor = MaterialTheme.colorScheme.onSurface,
                    weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    yearContentColor = MaterialTheme.colorScheme.onSurface,
                    currentYearContentColor = MaterialTheme.colorScheme.primary,
                    selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
                    selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                    dayContentColor = MaterialTheme.colorScheme.onSurface,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    todayContentColor = MaterialTheme.colorScheme.primary,
                    todayDateBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.performance_trip_history), color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { 
                        Icon(
                            painter = painterResource(id = AppIcons.Dashboard.ArrowLeft0), 
                            contentDescription = stringResource(R.string.trip_history_back), 
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp)
                        ) 
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.DateRange, 
                            contentDescription = "Calendar", 
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 1. A BARRA DE BOTÕES
            TripFilterBar(
                selectedFilter = selectedFilter,
                onFilterSelected = { novoFiltro ->
                    selectedFilter = novoFiltro
                    timeOffset = 0
                }
            )

            // 2. O NAVEGADOR DE TEMPO
            if (selectedFilter != "All") {
                TimeNavigator(
                    filterType = selectedFilter,
                    offset = timeOffset,
                    onOffsetChange = { timeOffset = it }
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. A LISTA (Vazia)
            if (trips.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.trip_history_no_trips), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trips) { trip ->
                        RecentTripRow(
                            date = trip.dateText,
                            route = trip.route,
                            distance = UnitConverter.formatDistance(trip.distanceKm, isMetric),
                            energy = trip.energy
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp), 
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeNavigator(
    filterType: String,
    offset: Int,
    onOffsetChange: (Int) -> Unit
) {
    val labelText = when (filterType) {
        "Day" -> {
            val today = LocalDate.now()
            val targetDate = today.plusDays(offset.toLong())
            if (offset == 0) stringResource(R.string.trip_history_today)
            else if (offset == -1) stringResource(R.string.trip_history_yesterday)
            else targetDate.format(DateTimeFormatter.ofPattern("dd MMM"))
        }
        "Week" -> {
            if (offset == 0) stringResource(R.string.trip_history_this_week)
            else if (offset == -1) stringResource(R.string.trip_history_last_week)
            else stringResource(R.string.trip_history_weeks_ago, Math.abs(offset))
        }
        "Month" -> {
            val today = LocalDate.now()
            val targetMonth = today.plusMonths(offset.toLong())
            if (offset == 0) stringResource(R.string.trip_history_this_month)
            else targetMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() } + " " + targetMonth.year
        }
        else -> ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onOffsetChange(offset - 1) }) {
            Icon(
                painter = painterResource(id = AppIcons.Dashboard.ArrowLeft0),
                contentDescription = stringResource(R.string.trip_history_previous),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = labelText,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        IconButton(
            onClick = { onOffsetChange(offset + 1) },
            enabled = offset < 0
        ) {
            Icon(
                painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                contentDescription = stringResource(R.string.trip_history_next),
                tint = if (offset < 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun TripFilterBar(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Day", "Week", "Month", "All")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = selectedFilter == filter
            val label = when (filter) {
                "Day" -> stringResource(R.string.trip_history_filter_day)
                "Week" -> stringResource(R.string.trip_history_filter_week)
                "Month" -> stringResource(R.string.trip_history_filter_month)
                "All" -> stringResource(R.string.trip_history_filter_all)
                else -> filter
            }
            Surface(
                modifier = Modifier.weight(1f).height(32.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                onClick = { onFilterSelected(filter) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

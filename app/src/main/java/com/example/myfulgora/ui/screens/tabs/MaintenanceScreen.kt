package com.example.myfulgora.ui.screens.tabs

import androidx.compose.animation.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.math.absoluteValue
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.example.myfulgora.R
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.theme.GreenFresh
import com.example.myfulgora.ui.theme.RedError
import java.util.Locale
import kotlin.math.absoluteValue

enum class MaintenanceStatus {
    DUE_SOON, SCHEDULED, PLANNED, COMPLETED
}

data class MaintenanceItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val date: String,
    val kmTrigger: Int,
    val daysRemaining: Int?,
    val kmRemaining: Int?,
    val serviceCenter: String,
    val estimatedTime: String,
    val estimatedCost: String,
    val status: MaintenanceStatus,
    val icon: Int,
    val accentColor: Color,
    val bgText: String,
    val isBooked: Boolean = false,
    val isNotified: Boolean = false
)

@Composable
fun MaintenanceScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showBookingSheet by remember { mutableStateOf(false) }
    
    val items = remember {
        mutableStateListOf(
            MaintenanceItem(
                1, "10 000 km Service", "15 MAY 2026",
                "Belt, brakes and battery inspection", "15 May 2026", 10000, 25, 3160,
                "Fulgora Lisboa", "2 h", "€220", MaintenanceStatus.DUE_SOON,
                AppIcons.Performance.next_service, RedError, "10k"
            ),
            MaintenanceItem(
                2, "Brake Pads", "20 JUNE 2026",
                "Front brake pad replacement", "20 June 2026", 8000, 61, 1160,
                "Fulgora Lisboa", "1 h", "€120", MaintenanceStatus.SCHEDULED,
                AppIcons.Performance.next_service, Color(0xFFFFB300), "8k"
            ),
            MaintenanceItem(
                3, "Tire Rotation", "5 AUGUST 2026",
                "Front / rear swap & pressure", "5 August 2026", 12000, 107, 5160,
                "Any Fulgora partner", "20 min", "€35", MaintenanceStatus.PLANNED,
                AppIcons.Performance.next_service, Color(0xFF9E9E9E), "12k"
            ),
            MaintenanceItem(
                4, "Battery Diagnostic", "10 NOVEMBER 2026",
                "Full cell health report", "10 November 2026", 15000, 204, 8160,
                "Fulgora Lisboa", "40 min", "€60", MaintenanceStatus.PLANNED,
                AppIcons.Battery.BatteryHealth, Color(0xFF9E9E9E), "15k"
            ),
            MaintenanceItem(
                5, "Oil & Filter", "2 MARCH 2026",
                "Transmission fluid refresh", "2 March 2026", 5000, -49, null,
                "Fulgora Lisboa", "45 min", "€80", MaintenanceStatus.COMPLETED,
                AppIcons.Performance.next_service, GreenFresh, "5k"
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { items.size })

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = GreenFresh,
                    contentColor = Color.Black,
                    snackbarData = data,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapPositionalThreshold = 0.15f 
                )
            ) { page ->
                MaintenanceStoryPage(
                    item = items[page],
                    onBookClick = { showBookingSheet = true },
                    onNotifyClick = {
                        val currentItem = items[page]
                        val isNowNotified = !currentItem.isNotified
                        items[page] = currentItem.copy(isNotified = isNowNotified)
                        
                        val msg = if (isNowNotified) "Notificação ativada" else "Notificação desativada"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                )
            }

            // Top Controls
            StoryTopBar(
                currentStep = pagerState.currentPage,
                totalSteps = items.size,
                onBackClick = onBackClick
            )

            // Bottom Navigation Hint (Ajustado para ficar acima da BottomBar)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 110.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.dropdown_arrow),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    modifier = Modifier.size(12.dp).graphicsLayer(rotationZ = 180f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.maintenance_swipe),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    fontSize = 9.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showBookingSheet) {
        MaintenanceBookingSheet(
            onDismiss = { showBookingSheet = false },
            onConfirm = { _, _, _ -> 
                val index = pagerState.currentPage
                val wasBooked = items[index].isBooked
                items[index] = items[index].copy(isBooked = true)
                showBookingSheet = false 
                
                val msg = if (wasBooked) "Agendamento alterado com sucesso" else "Manutenção agendada com sucesso"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun MaintenanceStoryPage(
    item: MaintenanceItem,
    onBookClick: () -> Unit,
    onNotifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background Glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            item.accentColor.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(540f, 960f),
                        radius = 1200f
                    )
                )
        )

        // Big Background Text (Watermark) - Ainda maior e mais opaco como no mockup
        Text(
            text = item.bgText,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 80.dp)
                .alpha(0.06f),
            fontSize = 380.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            softWrap = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
                .padding(top = 56.dp, bottom = 190.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Status Chip
                StatusChip(status = item.status, accentColor = item.accentColor)

                // Espaçamento fixo
                Spacer(modifier = Modifier.height(32.dp))

                // Main Stats (Countdown)
                if (item.status == MaintenanceStatus.COMPLETED) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(GreenFresh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painter = painterResource(id = R.drawable.info_status), contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.maintenance_done),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Column {
                        Text(
                            text = "Faltam:",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = (item.kmRemaining ?: 0).toString(),
                                color = item.accentColor,
                                fontSize = 80.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 80.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Km",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 14.dp)
                            )
                        }
                    }
                }

                // Reduzido de 40.dp para 24.dp
                Spacer(modifier = Modifier.height(24.dp))

                // Service Info
                Text(
                    text = item.subtitle,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = item.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )

                // Reduzido de 32.dp para 20.dp
                Spacer(modifier = Modifier.height(20.dp))

                // Details Table
                DetailRow(stringResource(R.string.maintenance_service_center), item.serviceCenter)
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))
                DetailRow(stringResource(R.string.maintenance_estimated_time), item.estimatedTime)
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))
                DetailRow(stringResource(R.string.maintenance_estimated_cost), item.estimatedCost)
            }

            // Espaçamento antes dos botões reduzido de 24.dp para 16.dp
            Spacer(modifier = Modifier.height(16.dp))

            // Action Button
            if (item.status == MaintenanceStatus.COMPLETED) {
                OutlinedButton(
                    onClick = { /* View Report */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f))
                ) {
                    Text(stringResource(R.string.maintenance_view_report), color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onBookClick,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenFresh),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(id = R.drawable.ic_map_selected), contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (item.isBooked) "Alterar Agendamento" else stringResource(R.string.maintenance_book_appointment),
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    FilledIconButton(
                        onClick = onNotifyClick,
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (item.isNotified) GreenFresh else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = if (item.isNotified) Icons.Default.Notifications else Icons.Default.Notifications, 
                            contentDescription = null, 
                            tint = if (item.isNotified) Color.Black else MaterialTheme.colorScheme.onBackground, 
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceBookingSheet(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    // Cores específicas solicitadas
    val darkBg = Color(0xFF121212)
    val surfaceDark = Color(0xFF1E1E1E)
    val grayBorder = Color(0xFF2C2C2C)

    // Estados de Seleção
    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var selectedTime by remember { mutableStateOf("10:30") }
    var selectedMobility by remember { mutableStateOf("Aguardar na oficina") }

    // Dados Mockados
    val next14Days = remember { (1..14).map { LocalDate.now().plusDays(it.toLong()) } }
    val availableTimes = listOf("09:00", "10:30", "14:00", "16:00", "17:30")
    val mobilityOptions = listOf("Aguardar na oficina", "Mota de substituição", "Vou de transporte")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = darkBg,
        scrimColor = Color.Black.copy(alpha = 0.75f),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray.copy(alpha = 0.5f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = stringResource(R.string.maintenance_book_appointment),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Calendário Horizontal
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(next14Days) { date ->
                    val isSelected = date == selectedDate
                    DateItem(
                        date = date,
                        isSelected = isSelected,
                        onClick = { selectedDate = date },
                        surfaceDark = surfaceDark,
                        grayBorder = grayBorder
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Horários
            Text(
                text = "Horários Disponíveis",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                availableTimes.take(4).forEach { time ->
                    TimeChip(
                        time = time,
                        isSelected = time == selectedTime,
                        onClick = { selectedTime = time },
                        surfaceDark = surfaceDark,
                        grayBorder = grayBorder
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Opções de Mobilidade
            Text(
                text = "Como planeia deslocar-se?",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            mobilityOptions.forEach { option ->
                MobilityOption(
                    label = option,
                    isSelected = option == selectedMobility,
                    onClick = { selectedMobility = option },
                    surfaceDark = surfaceDark
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Botão Confirmar
            Button(
                onClick = { onConfirm(selectedDate, selectedTime, selectedMobility) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenFresh),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Confirmar Agendamento",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun DateItem(date: LocalDate, isSelected: Boolean, onClick: () -> Unit, surfaceDark: Color, grayBorder: Color) {
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt")).replace(".", "")
    
    Column(
        modifier = Modifier
            .width(60.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) GreenFresh else surfaceDark)
            .border(
                width = 1.dp,
                color = if (isSelected) GreenFresh else grayBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayName.uppercase(),
            fontSize = 12.sp,
            color = if (isSelected) Color.Black else Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 20.sp,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun TimeChip(time: String, isSelected: Boolean, onClick: () -> Unit, surfaceDark: Color, grayBorder: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) GreenFresh else surfaceDark)
            .border(1.dp, if (isSelected) GreenFresh else grayBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun MobilityOption(label: String, isSelected: Boolean, onClick: () -> Unit, surfaceDark: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceDark)
            .border(
                width = 1.dp,
                color = if (isSelected) GreenFresh else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = GreenFresh,
                unselectedColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, color = Color.White, fontSize = 15.sp)
    }
}

@Composable
fun StatusChip(status: MaintenanceStatus, accentColor: Color) {
    val text = when (status) {
        MaintenanceStatus.DUE_SOON -> stringResource(R.string.maintenance_due_soon)
        MaintenanceStatus.SCHEDULED -> stringResource(R.string.scheduled)
        MaintenanceStatus.PLANNED -> stringResource(R.string.maintenance_planned)
        MaintenanceStatus.COMPLETED -> stringResource(R.string.completed)
    }.uppercase(Locale.getDefault())

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(accentColor.copy(alpha = 0.1f))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = accentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f), fontSize = 14.sp)
        Text(text = value, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StoryTopBar(
    currentStep: Int,
    totalSteps: Int,
    onBackClick: () -> Unit
) {
    // Aumentado o padding do topo para descolar da barra do sistema
    Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Seta personalizada e Título removido
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_bike_left), // 👈 Coloca aqui o teu ícone
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = String.format(Locale.getDefault(), "%02d / %02d", currentStep + 1, totalSteps),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun MaintenanceScreenPreview() {
    MaintenanceScreen(onBackClick = {})
}

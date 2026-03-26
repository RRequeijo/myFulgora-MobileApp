package com.example.myfulgora.ui.screens.tabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfulgora.R
import com.example.myfulgora.data.model.BikeState
import com.example.myfulgora.data.auth.UserManager
import androidx.compose.ui.platform.LocalContext
import com.example.myfulgora.data.helpers.SettingsManager
import kotlin.math.roundToInt
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.animation.core.animateDpAsState

@Composable
fun HomeScreen(
    state: BikeState,
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val isMetric by settingsManager.isMetricFlow.collectAsState(initial = true)

    val currentUser = UserManager.currentUser

    // Estado local para o modo de condução e motor
    var selectedMode by remember { mutableStateOf("Normal") }
    var isBikeOn by remember { mutableStateOf(false) }
    var showModeMenu by remember { mutableStateOf(false) }
    var showPowerDialog by remember { mutableStateOf(false) }
    val modes = listOf("Eco", "Normal", "Sport")
    val bottomNavHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // O PagerState controla a posição do slide (Começa no 1, que é o "Normal")
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { modes.size }
    )

    // Sincroniza a página atual do slide com a tua variável selectedMode
    LaunchedEffect(pagerState.currentPage) {
        selectedMode = modes[pagerState.currentPage]
    }


    FulgoraBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

            val screenW = this.maxWidth
            val iconSize = screenW * Dimens.IconScaleRatio
            val paddingSide = screenW * Dimens.SideMarginRatio
            val circleSize = screenW * Dimens.HomeCircleRatio
            val strokeWidth = 12.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = Dimens.TopPadding)
                    .padding(horizontal = paddingSide),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 1. TOP BAR
                FulgoraTopBar(
                    userName = currentUser?.profile?.name ?: "Rider",
                    iconSize = iconSize,
                    onMenuClick = onMenuClick,
                    onUserClick = onUserClick,
                    onCalendarClick = onCalendarClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. BIKE NAME & MINI STATUS
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = state.bikeName,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = AppIcons.Dashboard.BatteryTop),
                            contentDescription = null,
                            tint = GreenFresh,
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${state.batteryPercentage}%",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 3. CÍRCULO CENTRAL COM SETAS
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Setas de navegação (Mockup)
                    if (state.totalBikes > 1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = AppIcons.Dashboard.ArrowLeft0),
                                contentDescription = "Mota Anterior",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(36.dp)
                                    //.clickable { viewModel.motaAnterior() }
                                    .padding(4.dp)
                            )
                            Icon(
                                painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                                contentDescription = "Mota Seguinte",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(36.dp)
                                    //.clickable { viewModel.motaSeguinte() }
                                    .padding(4.dp)
                            )
                        }
                    }

                    // Círculo e Mota
                    Box(
                        modifier = Modifier.size(circleSize),
                        contentAlignment = Alignment.Center
                    ) {
                        // A. Arcos
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Fundo
                            drawArc(
                                color = Color.Gray.copy(alpha = 0.2f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth.toPx())
                            )
                            val sweep = (state.batteryPercentage * 360f) / 100f

                            // Progresso (Verde)
                            drawArc(
                                color = GreenFresh,
                                startAngle = -90f,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // B. A Mota (Sozinha no meio)
                        Image(
                            painter = painterResource(id = AppIcons.Dashboard.MainBike),
                            contentDescription = "My Bike",
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .aspectRatio(1.4f)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 4. LIGAR MOTA (40%) & MODO DE CONDUÇÃO (60%)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // --- 40%: BOTÃO DE LIGAR / DESLIGAR (STOP agora é vermelho) ---
                    Surface(
                        modifier = Modifier
                            .weight(0.4f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = if (isBikeOn) Color(0xFFE53935) else GreenFresh, // 👈 STOP = Vermelho, START = Verde
                        onClick = { showPowerDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = AppIcons.Dashboard.Power),
                                contentDescription = "Power",
                                tint = if (isBikeOn) Color.White else Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBikeOn) "STOP" else "START",
                                color = if (isBikeOn) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // --- 60%: SELETOR DE MODO DE CONDUÇÃO (SLIDE) ---
                    Surface(
                        modifier = Modifier
                            .weight(0.6f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant // 👈 Dinâmico
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // 1. O Texto Deslizável
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) { page ->
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = modes[page],
                                        color = if (pagerState.currentPage == page) GreenFresh else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (pagerState.currentPage == page) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            // 2. Bolinhas Indicadoras
                            Row(
                                modifier = Modifier.padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                modes.forEachIndexed { index, _ ->
                                    val isSelected = pagerState.currentPage == index
                                    val dotSize by animateDpAsState(
                                        targetValue = if (isSelected) 8.dp else 5.dp,
                                        label = "dotSizeAnim"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(dotSize)
                                            .clip(CircleShape)
                                            .background(if (isSelected) GreenFresh else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 5. INFOCARD DE STATUS
                FulgoraInfoCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HomeStatItem(
                            painterResource(id = AppIcons.Dashboard.Battery),
                            "${state.batteryPercentage}",
                            "%",
                        )
                        HomeStatItem(
                            painterResource(id = AppIcons.Dashboard.Power),
                            "${String.format("%.1f", state.consumption)}",
                            "kW/100"
                        )
                        val displayRange = if (isMetric) state.range else (state.range * 0.621371).roundToInt()
                        val unitLabel = if (isMetric) "km" else "mi"
                        HomeStatItem(
                            painterResource(id = AppIcons.Dashboard.Bike),
                            "$displayRange",
                            unitLabel
                        )
                        val isOnline = state.isOnline
                        HomeStatItem(
                            icon = painterResource(id = AppIcons.Dashboard.Status),
                            value = if (isOnline) stringResource(id = R.string.home_status_online) else stringResource(id = R.string.home_status_offline),
                            label = "",
                            statusColor = if (isOnline) GreenFresh else Color.Red
                        )
                    }
                }

                Spacer(modifier = Modifier.height(90.dp + bottomNavHeight))
            }

            // --- POPUP DE CONFIRMAÇÃO ---
            if (showPowerDialog) {
                AlertDialog(
                    onDismissRequest = { showPowerDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface, // 👈 Dinâmico
                    title = {
                        Text(
                            text = if (isBikeOn) stringResource(id = R.string.engine_stop_title) else stringResource(id = R.string.engine_start_title),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = if (isBikeOn) 
                                stringResource(id = R.string.engine_stop_message) 
                                else stringResource(id = R.string.engine_start_message),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                isBikeOn = !isBikeOn
                                showPowerDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBikeOn) Color(0xFFE53935) else GreenFresh
                            )
                        ) {
                            Text(text = stringResource(id = R.string.engine_start_confirm), color = if (isBikeOn) Color.White else Color.Black)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPowerDialog = false }) {
                            Text(stringResource(id = R.string.engine_start_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HomeStatItem(
    icon: Any,
    value: String,
    label: String,
    statusColor: Color? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when (icon) {
            is ImageVector -> Icon(icon, null, tint = GreenFresh, modifier = Modifier.size(24.dp))
            is Painter -> Icon(icon, null, tint = GreenFresh, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (statusColor != null) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface, // 👈 Dinâmico
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            if (label.isNotEmpty()) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp) // 👈 Dinâmico
            }
        }
    }
}

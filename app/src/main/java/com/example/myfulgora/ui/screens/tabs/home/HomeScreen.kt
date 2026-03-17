package com.example.myfulgora.ui.screens.tabs.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    state: BikeState,
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {}
) {
    // Isto obriga o ViewModel a buscar os dados frescos sempre que entras na Home
    LaunchedEffect(Unit) {
        viewModel.atualizarEcra()
    }

    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val isMetric by settingsManager.isMetricFlow.collectAsState(initial = true)

    val currentUser = UserManager.currentUser

    // Estado local para o modo de condução
    var selectedMode by remember { mutableStateOf("Normal") }

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
                    onUserClick = onUserClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. BIKE NAME & MINI STATUS
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = state.bikeName,
                        color = Color.White,
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
                            color = Color.White,
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
                                tint = Color.White,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { viewModel.motaAnterior() }
                                    .padding(4.dp)
                            )
                            Icon(
                                painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                                contentDescription = "Mota Seguinte",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { viewModel.motaSeguinte() }
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

                // 4. MODOS DE CONDUÇÃO (Eco, Normal, Sport)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val modes = listOf("Eco", "Normal", "Sport")

                    modes.forEach { mode ->
                        val isSelected = selectedMode == mode

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(28.dp),
                            color = if (isSelected) GreenFresh else Color(0xFF1E1E1E),
                            onClick = { selectedMode = mode }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 5. INFOCARD DE STATUS (4 colunas conforme o mockup)
                FulgoraInfoCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Bateria
                        HomeStatItem(
                            painterResource(id = AppIcons.Dashboard.Battery),
                            "${state.batteryPercentage}%",
                            "",
                        )

                        // 2. Consumo
                        HomeStatItem(
                            painterResource(id = AppIcons.Dashboard.Power),
                            "${state.consumption}",
                            "kW/100"
                        )

                        // 3. Autonomia (Range)
                        val displayRange = if (isMetric) state.range else (state.range * 0.621371).roundToInt()
                        val unitLabel = if (isMetric) "km" else "mi"
                        
                        HomeStatItem(
                            painterResource(id = AppIcons.Dashboard.Bike),
                            "$displayRange",
                            unitLabel
                        )

                        // 4. Status
                        val isOnline = state.isOnline

                        HomeStatItem(
                            icon = painterResource(id = AppIcons.Dashboard.Status),
                            value = if (isOnline) stringResource(id = R.string.home_status_online) else stringResource(id = R.string.home_status_offline),
                            label = "",
                            statusColor = if (isOnline) GreenFresh else Color.Red
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
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
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            if (label.isNotEmpty()) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = label, color = Color.Gray, fontSize = 10.sp)
            }
        }
    }
}
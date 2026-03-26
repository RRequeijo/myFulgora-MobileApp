package com.example.myfulgora.ui.screens.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.data.model.BikeState
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import androidx.compose.ui.res.stringResource
import com.example.myfulgora.R
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.helpers.SettingsManager
import com.example.myfulgora.data.helpers.UnitConverter
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfulgora.ui.viewmodel.MotaViewModel

@Composable
fun BatteryScreen(
    state: BikeState,
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val isMetric by settingsManager.isMetricFlow.collectAsState(initial = true)

    FulgoraBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

            val screenW = this.maxWidth
            val iconSize = screenW * Dimens.IconScaleRatio
            val paddingSide = screenW * Dimens.SideMarginRatio

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = paddingSide)
                    .padding(top = Dimens.TopPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 1. CABEÇALHO
                FulgoraTopBar(
                    userName = "${state.batteryTemp}",
                    iconSize = iconSize,
                    onMenuClick = onMenuClick,
                    onUserClick = onUserClick,
                    onCalendarClick = onCalendarClick
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                // 2. TÍTULO (Adaptável ao tema)
                Text(
                    text = stringResource(id = R.string.battery_title),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = Dimens.TextSizeHeader,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                // 3. BATERIA GRANDE + INFO
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BigBatteryIndicator(
                        state = state,
                        modifier = Modifier.weight(0.35f).fillMaxHeight()
                    )

                    Spacer(modifier = Modifier.width(Dimens.PaddingMedium))

                    Box(modifier = Modifier.weight(0.65f)) {
                        BatteryInfoCard(state = state, isMetric = isMetric)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // 4. GRELHA DE ESTATÍSTICAS
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                    ) {
                        BatteryStatCard(
                            icon = AppIcons.Battery.BatteryHealth,
                            title = stringResource(id = R.string.battery_health),
                            value = "${state.batteryHealth}",
                            modifier = Modifier.weight(1f)
                        )
                        BatteryStatCard(
                            icon = AppIcons.Battery.BatteryTemperature,
                            title = stringResource(id = R.string.battery_temperature),
                            value = "${String.format("%.1f", state.batteryTemp)}ºC",
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                    ) {
                        BatteryStatCard(
                            icon = AppIcons.Battery.BatteryConsumption,
                            title = stringResource(id = R.string.battery_consumption),
                            value = "${String.format("%.1f", state.consumption)} kW/100" + if(isMetric) "km" else "mi",
                            modifier = Modifier.weight(1f)
                        )
                        BatteryStatCard(
                            icon = AppIcons.Battery.BatteryChargingCycles,
                            title = stringResource(id = R.string.battery_charging_cycles),
                            value = "${state.batteryCycles} " + stringResource(id = R.string.battery_cycles),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.ScrollBottomPadding))
            }
        }
    }
}

@Composable
fun BigBatteryIndicator(
    state: BikeState,
    modifier: Modifier = Modifier
) {
    var showFrame1 by remember { mutableStateOf(true) }

    LaunchedEffect(state.isCharging) {
        if (state.isCharging) {
            while (true) {
                delay(800)
                showFrame1 = !showFrame1
            }
        } else {
            showFrame1 = true
        }
    }

    val currentIcon = if (state.isCharging) {
        if (showFrame1) AppIcons.Battery.BigBatteryCharging else AppIcons.Battery.BigBattery
    } else {
        AppIcons.Battery.BigBatteryCharging
    }

    val mainColor = if (state.isCharging) Color(0xFFFFD700) else GreenFresh

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1.2f))

        Icon(
            painter = painterResource(id = currentIcon),
            contentDescription = "Battery Status",
            tint = mainColor,
            modifier = Modifier.size(100.dp).scale(1.5f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // PERCENTAGEM (Adaptável ao tema)
        Text(
            text = "${state.batteryPercentage}%",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-1).sp
        )
    }
}

@Composable
fun BatteryInfoCard(
    state: BikeState,
    isMetric: Boolean
) {
    FulgoraInfoCard {
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = Dimens.PaddingMedium)
        ) {
            Text(
                text = if (state.isCharging) "Charging" else "Standby",
                color = if (state.isCharging) GreenFresh else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.time_left),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = Dimens.TextSizeSmall
            )
            Text(
                text = if (state.isCharging) state.timeLeft else "-- h -- m",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).aspectRatio(1f).padding(start = Dimens.PaddingMedium),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(painter = painterResource(id = AppIcons.Battery.Battery), contentDescription = null, tint = GreenFresh, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("${state.batteryPercentage}%", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeSmall)
            }
            Column(
                modifier = Modifier.weight(1f).aspectRatio(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(painter = painterResource(id = AppIcons.Battery.Range), contentDescription = null, tint = GreenFresh, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(UnitConverter.formatDistance(state.range, isMetric), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeSmall)
            }
            Column(
                modifier = Modifier.weight(1f).aspectRatio(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(painter = painterResource(id = AppIcons.Battery.Charging), contentDescription = null, tint = GreenFresh, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(if(state.isCharging) "On" else "Off", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeSmall)
            }
        }
    }
}

@Composable
fun BatteryStatCard(
    icon: Int,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    FulgoraInfoCard(modifier = modifier) {
        Icon(painter = painterResource(id = icon), contentDescription = null, tint = GreenFresh, modifier = Modifier.size(34.dp))
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeSmall)
    }
}

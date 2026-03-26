package com.example.myfulgora.ui.screens.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.R
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import com.example.myfulgora.data.auth.UserManager
import androidx.compose.ui.platform.LocalContext
import com.example.myfulgora.data.helpers.SettingsManager
import com.example.myfulgora.data.model.BikeState
import kotlin.math.roundToInt

@Composable
fun PerformanceScreen(
    state: BikeState,
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
) {
    val currentUser = UserManager.currentUser
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val isMetric by settingsManager.isMetricFlow.collectAsState(initial = true)
    val bottomNavHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

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
                    .padding(top = Dimens.TopPadding)
            ) {

                // 1. HEADER
                FulgoraTopBar(
                    userName = currentUser?.profile?.name ?: "Rider",
                    iconSize = iconSize,
                    onMenuClick = onMenuClick,
                    onUserClick = onUserClick,
                    onCalendarClick = onCalendarClick
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                // 2. TÍTULO E SUBTÍTULO
                Column {
                    Text(
                        text = stringResource(id = R.string.performance_title),
                        fontSize = Dimens.TextSizeHeader,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = state.bikeName,
                        fontSize = Dimens.TextSizeNormal,
                        color = GreenFresh
                    )
                }

                // 3. ZONA DA MOTO
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = AppIcons.Dashboard.ArrowLeft0),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                        Icon(
                            painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = AppIcons.Dashboard.MainBike),
                            contentDescription = "My Bike",
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .aspectRatio(1.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                // 4. ODOMETER CARD
                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(id = R.string.performance_odometer), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeTitle)
                            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

                            Row(verticalAlignment = Alignment.Bottom) {
                                val displayOdo = if (isMetric) "${state.totalKilometers} km" else "${(state.totalKilometers * 0.621371).roundToInt()} mi"
                                Text(
                                    text = displayOdo,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp
                                )
                            }
                        }

                        Icon(
                            painter = painterResource(id = AppIcons.Performance.odometer),
                            contentDescription = "Odometer",
                            tint = GreenFresh,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                // TYRES & SERVICE CARDS
                Row(modifier = Modifier.fillMaxWidth()) {
                    FulgoraInfoCard(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(end = Dimens.PaddingMedium)) {
                                Text(stringResource(id = R.string.performance_tyre_pressure), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeSubTitle)
                                Text(stringResource(id = R.string.performance_tyre_pressure_normal), color = GreenFresh, fontSize = Dimens.TextSizeSmall)
                                Spacer(modifier = Modifier.height(Dimens.SpacingSmallPlus))
                                Text("Front: ${state.tyreFront} bar", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = Dimens.TextSizeNormal)
                                Text("Rear: ${state.tyreBack} bar", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = Dimens.TextSizeNormal)
                            }
                            Icon(
                                painter = painterResource(id = AppIcons.Performance.tyre_pressure),
                                contentDescription = null,
                                tint = GreenFresh,
                                modifier = Modifier.size(24.dp).align(Alignment.TopEnd)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                    FulgoraInfoCard(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(end = Dimens.PaddingMedium)) {
                                Text(stringResource(id = R.string.performance_next_service), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeSubTitle)
                                Text(stringResource(id = R.string.performance_next_service_due), color = GreenFresh, fontSize = Dimens.TextSizeSmall)
                                Spacer(modifier = Modifier.height(Dimens.SpacingSmallPlus))
                                Text(stringResource(id = R.string.performance_next_service_in), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeNormal)

                                val serviceDistance = 5000 - (state.totalKilometers % 5000)
                                val displayService = if (isMetric) "$serviceDistance km" else "${(serviceDistance * 0.621371).roundToInt()} mi"

                                Text(displayService, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = Dimens.TextSizeNormal)
                            }
                            Icon(
                                painter = painterResource(id = AppIcons.Performance.next_service),
                                contentDescription = null,
                                tint = GreenFresh,
                                modifier = Modifier.size(24.dp).align(Alignment.TopEnd)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                // PERFORMANCE STATS CARD
                FulgoraInfoCard {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(end = 32.dp)) {
                                Text(stringResource(id = R.string.performance_title), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeTitle)
                            }
                            Spacer(modifier = Modifier.height(Dimens.SpacingSmallPlus))
                            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(id = R.string.performance_energy_consumption), color = MaterialTheme.colorScheme.onSurface, fontSize = Dimens.TextSizeSubTitle)
                                    Text(stringResource(id = R.string.performance_energy_consumption_last_7_days), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeNormal)

                                    val formattedConsumption = String.format("%.1f", state.avgConsumption)
                                    Text("$formattedConsumption kWh/100", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = Dimens.TextSizeSubTitle)
                                }
                                Box(modifier = Modifier.padding(horizontal = Dimens.PaddingMedium).width(1.dp).fillMaxHeight(0.8f).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(id = R.string.performance_average_speed), color = MaterialTheme.colorScheme.onSurface, fontSize = Dimens.TextSizeSubTitle)
                                    Text(stringResource(id = R.string.performance_average_speed_based_on_recent_trips), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeNormal)

                                    val avgSpeed = state.averageSpeed
                                    val displaySpeed = if (isMetric) "$avgSpeed km/h" else "${(avgSpeed * 0.621371).roundToInt()} mph"
                                    Text(displaySpeed, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = Dimens.TextSizeSubTitle)
                                }
                            }
                        }
                        Icon(painter = painterResource(id = AppIcons.Performance.performance), contentDescription = null, tint = GreenFresh, modifier = Modifier.size(24.dp).align(Alignment.TopEnd))
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                // SUSTAINABILITY CARD
                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(id = R.string.performance_sustainability), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeTitle)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(stringResource(id = R.string.performance_co2_emissions), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = Dimens.TextSizeSmall)

                            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

                            Row(verticalAlignment = Alignment.Bottom) {
                                val co2Saved = (state.totalKilometers * 0.115)
                                val displayCo2 = String.format("%.1f kg", co2Saved)

                                Text(
                                    text = displayCo2,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    stringResource(id = R.string.performance_saved),
                                    color = GreenFresh,
                                    fontSize = Dimens.TextSizeNormal,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Rounded.Eco,
                            contentDescription = "Sustainability",
                            tint = GreenFresh,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // 6. MARGEM DE SEGURANÇA PARA O MENU FLUTUANTE
                Spacer(modifier = Modifier.height(100.dp + bottomNavHeight))
            }
        }
    }
}

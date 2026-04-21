package com.example.myfulgora.ui.screens.tabs

import androidx.compose.animation.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    val bgText: String
)

@Composable
fun MaintenanceScreen(
    onBackClick: () -> Unit
) {
    val items = listOf(
        MaintenanceItem(
            1, "10 000 km Service", "10 000 KM • 15 MAY 2026",
            "Belt, brakes and battery inspection", "15 May 2026", 10000, 25, 3160,
            "Fulgora Lisboa", "2 h", "€220", MaintenanceStatus.DUE_SOON,
            AppIcons.Performance.next_service, RedError, "10k"
        ),
        MaintenanceItem(
            2, "Brake Pads", "8 000 KM • 20 JUNE 2026",
            "Front brake pad replacement", "20 June 2026", 8000, 61, 1160,
            "Fulgora Lisboa", "1 h", "€120", MaintenanceStatus.SCHEDULED,
            AppIcons.Performance.next_service, Color(0xFFFFB300), "8k"
        ),
        MaintenanceItem(
            3, "Tire Rotation", "12 000 KM • 5 AUGUST 2026",
            "Front / rear swap & pressure", "5 August 2026", 12000, 107, 5160,
            "Any Fulgora partner", "20 min", "€35", MaintenanceStatus.PLANNED,
            AppIcons.Performance.next_service, Color(0xFF9E9E9E), "12k"
        ),
        MaintenanceItem(
            4, "Battery Diagnostic", "15 000 KM • 10 NOVEMBER 2026",
            "Full cell health report", "10 November 2026", 15000, 204, 8160,
            "Fulgora Lisboa", "40 min", "€60", MaintenanceStatus.PLANNED,
            AppIcons.Battery.BatteryHealth, Color(0xFF9E9E9E), "15k"
        ),
        MaintenanceItem(
            5, "Oil & Filter", "5 000 KM • 2 MARCH 2026",
            "Transmission fluid refresh", "2 March 2026", 5000, -49, null,
            "Fulgora Lisboa", "45 min", "€80", MaintenanceStatus.COMPLETED,
            AppIcons.Performance.next_service, GreenFresh, "5k"
        )
    )

    val pagerState = rememberPagerState(pageCount = { items.size })

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            MaintenanceStoryPage(
                item = items[page],
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
                .padding(bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.dropdown_arrow),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(12.dp).graphicsLayer(rotationZ = 180f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.maintenance_swipe),
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MaintenanceStoryPage(
    item: MaintenanceItem,
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
            color = Color.White,
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

                // Reduzido de 24.dp para 16.dp
                Spacer(modifier = Modifier.height(16.dp))

                // Icon Container (Tamanho reduzido para poupar espaço)
                Box(
                    modifier = Modifier
                        .size(64.dp) // Era 90.dp
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.06f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = null,
                        tint = item.accentColor,
                        modifier = Modifier.size(28.dp) // Era 36.dp
                    )
                }

                // Reduzido de 32.dp para 16.dp
                Spacer(modifier = Modifier.height(16.dp))

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
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${item.daysRemaining?.absoluteValue} ${stringResource(R.string.maintenance_ago)}",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = item.daysRemaining.toString(),
                                color = item.accentColor,
                                fontSize = 80.sp, // Reduzido de 96.sp para caber melhor
                                fontWeight = FontWeight.Bold,
                                lineHeight = 80.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.maintenance_days),
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 14.dp)
                            )
                        }
                        Text(
                            text = "${stringResource(R.string.maintenance_until_service)} • ${item.kmRemaining} km ${stringResource(R.string.maintenance_away)}",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                }

                // Reduzido de 40.dp para 24.dp
                Spacer(modifier = Modifier.height(24.dp))

                // Service Info
                Text(
                    text = item.subtitle,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 28.sp, // Reduzido de 32.sp
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )

                // Reduzido de 32.dp para 20.dp
                Spacer(modifier = Modifier.height(20.dp))

                // Details Table
                DetailRow(stringResource(R.string.maintenance_service_center), item.serviceCenter)
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))
                DetailRow(stringResource(R.string.maintenance_estimated_time), item.estimatedTime)
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))
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
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Text(stringResource(R.string.maintenance_view_report), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { /* Book */ },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenFresh),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(id = R.drawable.ic_map_selected), contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(stringResource(R.string.maintenance_book_appointment), color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    FilledIconButton(
                        onClick = { /* Notify */ },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: MaintenanceStatus, accentColor: Color) {
    val text = when (status) {
        MaintenanceStatus.DUE_SOON -> stringResource(R.string.maintenance_due_soon)
        MaintenanceStatus.SCHEDULED -> stringResource(R.string.scheduled)
        MaintenanceStatus.PLANNED -> stringResource(R.string.maintenance_planned)
        MaintenanceStatus.COMPLETED -> stringResource(R.string.completed)
    }

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
        Text(text = label, color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp)
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = String.format(Locale.getDefault(), "%02d / %02d", currentStep + 1, totalSteps),
                color = Color.White.copy(alpha = 0.5f),
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

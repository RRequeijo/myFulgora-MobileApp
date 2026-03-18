package com.example.myfulgora.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.R
import com.example.myfulgora.data.model.FulgoraNotification
import com.example.myfulgora.ui.theme.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.unit.IntOffset
import com.example.myfulgora.data.helpers.NotificationManager

@Composable
fun FulgoraBackground(
    content: @Composable BoxScope.() -> Unit
) {
    val bottomGlow = Brush.radialGradient(
        colors = listOf(
            GreenDeep.copy(alpha = 0.5f),
            Color.Transparent
        ),
        center = Offset(x = 0f, y = Float.POSITIVE_INFINITY),
        radius = 1500f
    )

    val topGlow = Brush.radialGradient(
        colors = listOf(
            Color(0xFF1E293B).copy(alpha = 0.6f),
            Color.Transparent
        ),
        center = Offset(x = Float.POSITIVE_INFINITY, y = 0f),
        radius = 1200f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBrand)
            .background(bottomGlow)
            .background(topGlow)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FulgoraTopBar(
    greeting: String = "Hi", // O cumprimento inicial
    userName: String = "Rider", // O nome que vai vir do teu UserManager
    subtitle: String = stringResource(id = R.string.topbar_subtitle),
    iconSize: Dp = 24.dp,
    onNotificationClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {} // 👈 NOVO: Ação quando clica no nome
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            // Colocamos o "Hi," e o "Username" lado a lado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$greeting, ",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userName,
                    color = GreenFresh,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onUserClick() }
                )
            }
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val notificationsList by NotificationManager.notifications.collectAsState()
        val unreadCount = notificationsList.count { !it.isRead }

        Row(verticalAlignment = Alignment.CenterVertically) {

            var showNotifications by remember { mutableStateOf(false) }

            BadgedBox(
                badge = {
                    if (unreadCount > 0) {
                        Badge(containerColor = Color.Red, contentColor = Color.White) {
                            Text(text = unreadCount.toString())
                        }
                    }
                },
                modifier = Modifier
                    .padding(end = 20.dp)
                    .clickable { showNotifications = !showNotifications }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize)
                )
            }

            // 👇 2. O NOVO POPUP MODERNO E LARGO 👇
            if (showNotifications) {
                Popup(
                    alignment = Alignment.TopEnd,
                    offset = IntOffset(0, 120),
                    onDismissRequest = { showNotifications = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    Card(
                        modifier = Modifier
                            .width(screenWidth - 32.dp)
                            .padding(end = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Notificações", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(
                                    "Marcar como lidas",
                                    color = GreenFresh,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        NotificationManager.markAllAsRead()
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))

                            if (notificationsList.isEmpty()) {
                                Text(
                                    text = "Não tens notificações recentes.",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    notificationsList.take(5).forEach { notif ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Ponto indicador de "não lida"
                                            if (!notif.isRead) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(GreenFresh, RoundedCornerShape(50))
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(notif.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(notif.message, color = Color.Gray, fontSize = 12.sp)
                                                Text(notif.time, color = GreenFresh.copy(alpha = 0.7f), fontSize = 10.sp)
                                            }
                                        }
                                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier
                    .size(iconSize)
                    .clickable { onMenuClick() }
            )
        }
    }
}

@Composable
fun FulgoraInfoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CardBackgroundColor,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun CircularBatteryArc(percentage: Float) {
    Canvas(modifier = Modifier.size(300.dp)) {
        drawArc(
            color = Color(0xFF1E293B),
            startAngle = 140f,
            sweepAngle = 260f,
            useCenter = false,
            style = Stroke(width = 15f, cap = StrokeCap.Round)
        )
        drawArc(
            color = GreenFresh,
            startAngle = 140f,
            sweepAngle = 260f * percentage,
            useCenter = false,
            style = Stroke(width = 15f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun DashboardStat(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = GreenFresh, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun FulgoraDrawerItem(
    label: String,
    painter: Painter,
    onClick: () -> Unit,
    selected: Boolean = false,
    textColor: Color = Color.White,
    iconColor: Color = Color.White
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        },
        label = { Text(label, color = textColor, fontSize = 16.sp) },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = Color(0xFF2D2D2D)
        ),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
fun FulgoraDrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    selected: Boolean = false,
    textColor: Color = Color.White,
    iconColor: Color = Color.White
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null, tint = iconColor) },
        label = { Text(label, color = textColor) },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = Color(0xFF2D2D2D)
        ),
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}

@Composable
fun RecentTripRow(
    date: String,
    route: String,
    distance: String,
    energy: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lado Esquerdo: Rota e Data
        Column {
            Text(text = route, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = date, color = Color.Gray, fontSize = 12.sp)
        }

        // Lado Direito: Distância e Energia
        Column(horizontalAlignment = Alignment.End) {
            Text(text = distance, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = energy, color = GreenFresh, fontSize = 12.sp) // Coloquei a verde para dar destaque!
        }
    }
}

@Composable
fun TripFilterBar(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Hoje", "Semana", "Mês", "Tudo")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
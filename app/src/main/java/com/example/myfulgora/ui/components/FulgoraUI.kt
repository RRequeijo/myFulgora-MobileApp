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
import androidx.compose.ui.platform.LocalConfiguration // 👈 Adiciona este import
import androidx.compose.ui.window.Popup // 👈 Adiciona este import
import androidx.compose.ui.window.PopupProperties // 👈 Adiciona este import
import androidx.compose.ui.unit.IntOffset // 👈 Adiciona este import
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
    unreadNotifications: Int = 0,
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

        // Bloco dos Ícones (Direita)
        // Descobrimos a largura do ecrã do telemóvel
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        // 1. Fica a ouvir a lista real de notificações
        val notificationsList by NotificationManager.notifications.collectAsState()

        // 2. Conta matematicamente quantas não estão lidas
        val unreadNotifications = notificationsList.count { !it.isRead }

        // Bloco dos Ícones (Direita)
        Row(verticalAlignment = Alignment.CenterVertically) {

            var showNotifications by remember { mutableStateOf(false) }

            // 1. O Sino com as Notificações
            BadgedBox(
                badge = {
                    if (unreadNotifications > 0) {
                        Badge(containerColor = Color.Red, contentColor = Color.White) {
                            Text(text = unreadNotifications.toString())
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
                    offset = IntOffset(0, 100), // Empurra o cartão para baixo do sino
                    onDismissRequest = { showNotifications = false },
                    properties = PopupProperties(focusable = true) // Permite clicar fora para fechar
                ) {
                    Card(
                        modifier = Modifier
                            .width(screenWidth - 32.dp) // Largura do ecrã menos 16dp de margem de cada lado
                            .padding(end = 16.dp), // Margem direita para não colar ao limite
                        shape = RoundedCornerShape(16.dp), // Cantos bem arredondados!
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), // Fundo escuro premium
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp) // Sombra flutuante
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Cabeçalho do Cartão
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Notificações", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(
                                    "Fechar",
                                    color = GreenFresh,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable {
                                            showNotifications = !showNotifications
                                            if (showNotifications) {
                                                // Se abriu o popup, avisa o cérebro que o utilizador já viu!
                                                NotificationManager.markAllAsRead()
                                            }
                                            onNotificationClick()
                                        }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))


                            // Se a lista estiver vazia, mostramos uma mensagem simpática
                            if (notificationsList.isEmpty()) {
                                Text(
                                    text = "Não tens notificações recentes.",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            } else {
                                // Desenha as notificações reais!
                                notificationsList.forEach { notif ->
                                    // ... (o teu código do cartão da notificação fica exatamente igual aqui dentro)
                                }
                            }
                        }
                    }
                }
            }

            // O teu Menu Hambúrguer (exatamente como estava)
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

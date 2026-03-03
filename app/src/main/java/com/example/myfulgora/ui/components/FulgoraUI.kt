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
import androidx.compose.runtime.Composable
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
import com.example.myfulgora.ui.theme.*

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
                    color = GreenFresh, // 👈 Destaca o nome a verde (importa a cor se necessário)
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onUserClick() } // 👈 Torna só o nome clicável
                )
            }
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Row {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.White,
                modifier = Modifier
                    .size(iconSize)
                    .clickable { onNotificationClick() }
            )

            Spacer(modifier = Modifier.width(16.dp))

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

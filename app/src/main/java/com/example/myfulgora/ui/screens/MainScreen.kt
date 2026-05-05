package com.example.myfulgora.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.myfulgora.R
import com.example.myfulgora.data.auth.AuthManager
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.model.BikeState
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.screens.tabs.*
import com.example.myfulgora.ui.screens.tabs.map.MapScreen
import com.example.myfulgora.ui.screens.tabs.DocumentationScreen
import com.example.myfulgora.ui.screens.tabs.ProfileScreen
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.viewmodel.HomeUiState
import com.example.myfulgora.ui.viewmodel.MotaViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val viewModel: MotaViewModel = viewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        DrawerItemData(R.string.navbar_home, AppIcons.Navbar.HomeUnselected, "home"),
        DrawerItemData(R.string.navbar_profile, Icons.Outlined.Person, "profile"),
        DrawerItemData(R.string.navbar_map, AppIcons.Navbar.MapUnselected, "map"),
        DrawerItemData(R.string.navbar_battery, AppIcons.Navbar.BatteryUnselected, "battery"),
        //DrawerItemData(R.string.navbar_social, AppIcons.Navbar.Social, "social"),
        DrawerItemData(R.string.navbar_performance, AppIcons.Navbar.PerformanceUnselected, "performance"),
        DrawerItemData(R.string.navbar_delegation, AppIcons.Menu.Delegation, "delegation"),
        DrawerItemData(R.string.navbar_settings, AppIcons.Menu.Settings, "settings")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigateToProfile = {
        navController.navigate("profile") {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val currentBikeState = when (val state = uiState) {
        is HomeUiState.Success -> state.bikeState
        else -> BikeState()
    }

    FulgoraBackground {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = drawerState.isOpen,
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        ModalDrawerSheet(
                            drawerShape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                            drawerContainerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.width(320.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize().padding(vertical = 24.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Image(painter = painterResource(id = R.drawable.logo_app), contentDescription = "myFULGORA", modifier = Modifier.height(100.dp))
                                    Spacer(modifier = Modifier.weight(1.2f))
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    menuItems.forEach { item ->
                                        DrawerItem(
                                            label = stringResource(id = item.title),
                                            icon = item.icon,
                                            selected = currentRoute == item.route,
                                            onClick = {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                                scope.launch { drawerState.close() }
                                            }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                DrawerItem(label = stringResource(R.string.navbar_help), icon = AppIcons.Menu.Help, onClick = { scope.launch { drawerState.close() }; context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://amover.utad.pt/"))) })
                                DrawerItem(
                                    label = stringResource(R.string.navbar_logout),
                                    icon = AppIcons.Menu.Logout,
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                            authManager.logout()
                                            UserManager.logout()
                                            onLogout()
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.fillMaxSize().statusBarsPadding()
                        ) {
                            composable("map") { MapScreen(state = currentBikeState, onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }, onCalendarClick = { navController.navigate("maintenance_screen") }) }
                            composable("history") { TripHistoryScreen(onBackClick = { navController.popBackStack() }) }
                            composable("profile") { ProfileScreen(navController = navController, onMenuClick = { scope.launch { drawerState.open() } }, onCalendarClick = { navController.navigate("maintenance_screen") }) }
                            composable("battery") { BatteryScreen(state = currentBikeState, onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }, onCalendarClick = { navController.navigate("maintenance_screen") }) }
                            composable("home") { 
                                HomeScreen(
                                    state = currentBikeState, 
                                    onMenuClick = { scope.launch { drawerState.open() } }, 
                                    onUserClick = { navigateToProfile() }, 
                                    onCalendarClick = { navController.navigate("maintenance_screen") },
                                    onModeChange = { newMode -> viewModel.setDrivingMode(newMode) }
                                ) 
                            }
                            composable("maintenance_screen") { MaintenanceScreen(onBackClick = { navController.popBackStack() }) }
                            composable("social") { SocialScreen(onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }, onCalendarClick = { navController.navigate("maintenance_screen") }) }
                            composable("performance") { PerformanceScreen(state = currentBikeState, onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }, onCalendarClick = { navController.navigate("maintenance_screen") }) }
                            composable("delegation") { DelegationScreen(onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }, onCalendarClick = { navController.navigate("maintenance_screen") }) }
                            composable("settings") { SettingsScreen(onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }, onCalendarClick = { navController.navigate("maintenance_screen") }) }
                            composable("documentation") { 
                                DocumentationScreen(
                                    state = currentBikeState, 
                                    onSaveDocument = { nome, uri -> viewModel.guardarDocumento(nome, uri) },
                                    onMenuClick = { scope.launch { drawerState.open() } },
                                    onUserClick = { navigateToProfile() },
                                    onCalendarClick = { navController.navigate("maintenance_screen") }
                                ) 
                            }
                        }

                        // Gradiente de desfoque corrigido para usar a cor inteligente
                        val bgColor = MaterialTheme.colorScheme.background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            bgColor.copy(alpha = 0.0f),
                                            bgColor.copy(alpha = 0.6f),
                                            bgColor.copy(alpha = 1.0f)
                                        )
                                    )
                                )
                                .align(Alignment.BottomCenter)
                        )

                        FulgoraPillBottomBar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding(),
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FulgoraPillBottomBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val navItems = listOf(
        NavItemData("map", R.string.navbar_map, AppIcons.Navbar.MapUnselected, AppIcons.Navbar.Map),
        NavItemData("battery", R.string.navbar_battery, AppIcons.Navbar.BatteryUnselected, AppIcons.Navbar.Battery),
        NavItemData("home", R.string.navbar_home, AppIcons.Navbar.HomeUnselected, AppIcons.Navbar.Home),
        NavItemData("performance", R.string.navbar_performance, AppIcons.Navbar.PerformanceUnselected, AppIcons.Navbar.Performance)
    )

    // Superfície principal da barra (fixa no fundo, cantos superiores arredondados)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface, // Agora usa a cor do tema (clara ou escura)
        tonalElevation = 4.dp // Adiciona uma leve elevação para destacar no modo claro
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                PillNavItem(item = item, isSelected = isSelected, onClick = { onNavigate(item.route) })
            }
        }
    }
}

@Composable
fun PillNavItem(item: NavItemData, isSelected: Boolean, onClick: () -> Unit) {
    // Animação do fundo do item selecionado (adapta-se ao tema)
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(300),
        label = "nav_item_bg"
    )
    
    // Todos os ícones usam a cor primária (GreenFresh) como na imagem
    val contentColor = MaterialTheme.colorScheme.primary
    val iconToDisplay = if (isSelected) item.iconSelected else item.iconUnselected

    val iconSize = if (item.route == "home") 32.dp else 24.dp

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        val iconModifier = Modifier.size(iconSize)
        when (iconToDisplay) {
            is ImageVector -> {
                Icon(
                    imageVector = iconToDisplay,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = iconModifier
                )
            }
            is Int -> {
                Icon(
                    painter = painterResource(id = iconToDisplay),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = iconModifier
                )
            }
        }
    }
}

data class NavItemData(val route: String, val labelRes: Int, val iconUnselected: Any, val iconSelected: Any)
data class DrawerItemData(val title: Int, val icon: Any, val route: String)

@Composable
fun DrawerItem(label: String, icon: Any, selected: Boolean = false, onClick: () -> Unit) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = color, fontSize = 18.sp, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal)
        Spacer(modifier = Modifier.width(16.dp))
        when (icon) {
            is ImageVector -> Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            is Int -> Icon(painterResource(id = icon), null, tint = color, modifier = Modifier.size(24.dp))
        }
    }
}
package com.example.myfulgora.ui.screens.tabs

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.myfulgora.data.model.BikeState
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.screens.tabs.map.MapScreen
import com.example.myfulgora.ui.screens.tabs.profile.DocumentationScreen
import com.example.myfulgora.ui.screens.tabs.profile.ProfileScreen
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.viewmodel.HomeUiState
import com.example.myfulgora.ui.viewmodel.MotaViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel: MotaViewModel = viewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        DrawerItemData(R.string.navbar_home, AppIcons.Navbar.Home, "home"),
        DrawerItemData(R.string.navbar_profile, Icons.Outlined.Person, "profile"),
        DrawerItemData(R.string.navbar_map, AppIcons.Navbar.Map, "map"),
        DrawerItemData(R.string.navbar_battery, AppIcons.Navbar.Battery, "battery"),
        DrawerItemData(R.string.navbar_social, AppIcons.Navbar.Social, "social"),
        DrawerItemData(R.string.navbar_performance, AppIcons.Navbar.Performance, "performance"),
        DrawerItemData(R.string.navbar_delegation, AppIcons.Menu.Delegation , "delegation"),
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

    val currentBikeState = when(val state = uiState) {
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
                                DrawerItem(label = stringResource(R.string.navbar_logout), icon = AppIcons.Menu.Logout, onClick = { /* Logout */ })
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    // 👇 1. Trocámos a Box por um Scaffold!
                    // 👇 1. MUDANÇA ARQUITETURAL CRUCIAL 👇
                    // Usamos uma Box para o menu flutuar livremente sobre o NavHost
                    Box(modifier = Modifier.fillMaxSize()) {

                        // O NavHost ocupa tudo e ignora o padding inferior
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding() // Protege apenas o topo
                            // .navigationBarsPadding() // 🔥 REMOVIDO para content passar por trás
                        ) {
                            composable("map") { MapScreen(state = currentBikeState, onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }, onCalendarClick = { navController.navigate("maintenance_screen") }, onViewAllClick = { navController.navigate("history") }) }
                            composable("history") { TripHistoryScreen(onBackClick = { navController.popBackStack() }) }
                            composable("profile") { ProfileScreen(navController = navController, onMenuClick = { scope.launch { drawerState.open() } }) }
                            composable("battery") { BatteryScreen(state = currentBikeState, onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }, onCalendarClick = { navController.navigate("maintenance_screen") }) }
                            composable("home") { HomeScreen(state = currentBikeState, onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }, onCalendarClick = { navController.navigate("maintenance_screen") }) }
                            composable("maintenance_screen") { MaintenanceScreen(onBackClick = { navController.popBackStack() }) }
                            composable("social") { SocialScreen(onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }, onCalendarClick = { navController.navigate("maintenance_screen") }) }
                            composable("performance") { PerformanceScreen(state = currentBikeState, onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }) }
                            composable("delegation") { DelegationScreen(onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }) }
                            composable("settings") { SettingsScreen(onMenuClick = { scope.launch { drawerState.open() } }, onUserClick = { navigateToProfile() }) }
                            composable("documentation") { DocumentationScreen(state = currentBikeState, onSaveDocument = { nome, uri -> viewModel.guardarDocumento(nome, uri) }) }
                        }

                        // 👇 2. O GRADIENTE DE DESFOQUE DO REVOLUT 👇
                        // Esta Box fica entre o conteúdo e o menu, criando o fade para preto
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp) // A altura total do efeito
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF1A1A1A).copy(alpha = 0.0f), // Começa transparente
                                            Color(0xFF1A1A1A).copy(alpha = 0.5f), // Fica foscado
                                            Color(0xFF000000).copy(alpha = 1.0f)  // Full preto no fim
                                        )
                                    )
                                )
                                .align(Alignment.BottomCenter) // Cola ao fundo do ecrã
                        )

                        // 3. O MENU REVOLUT (Com efeito de vidro)
                        FulgoraPillBottomBar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding(), // Protege os gestos do telemóvel
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
        NavItemData("map", R.string.navbar_map, AppIcons.Navbar.Map),
        NavItemData("battery", R.string.navbar_battery, AppIcons.Navbar.Battery),
        NavItemData("home", R.string.navbar_home, AppIcons.Navbar.Home),
        NavItemData("performance", R.string.navbar_performance, AppIcons.Navbar.Performance)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(32.dp),
            // 👇 4. EFEITO DE VIDRO FOSCADO (Frosted Glass) 👇
            // Em vez de MaterialTheme.surface (que é opaco), usamos uma cor translúcida
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp, // Removemos tonal para não estragar a transparência
            shadowElevation = 12.dp // Sombra mais forte para destacar o "vidro"
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
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
}

@Composable
fun PillNavItem(item: NavItemData, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f) else Color.Transparent, animationSpec = tween(300), label = "")
    val contentColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, label = "")
    Column(
        modifier = Modifier.clip(CircleShape).background(backgroundColor).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick).padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(painter = painterResource(id = item.icon), contentDescription = stringResource(id = item.labelRes), tint = contentColor, modifier = Modifier.size(24.dp))
        Text(text = stringResource(id = item.labelRes), color = contentColor, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
    }
}

data class NavItemData(val route: String, val labelRes: Int, val icon: Int)
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

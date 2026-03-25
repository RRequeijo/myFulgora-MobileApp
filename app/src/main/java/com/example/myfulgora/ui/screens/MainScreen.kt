package com.example.myfulgora.ui.screens.tabs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.myfulgora.ui.screens.tabs.map.MapScreen
import com.example.myfulgora.ui.screens.tabs.profile.DocumentationScreen
import com.example.myfulgora.ui.screens.tabs.profile.ProfileScreen
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.theme.BlackBrand
import com.example.myfulgora.ui.theme.GreenFresh
import com.example.myfulgora.ui.viewmodel.HomeUiState
import com.example.myfulgora.ui.viewmodel.MotaViewModel
import kotlinx.coroutines.launch

// Classe auxiliar atualizada para aceitar Painter ou ImageVector
data class DrawerItemData(
    val title: Int,
    val icon: Any,
    val route: String
)

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

    // 👇 ESTA É A FUNÇÃO QUE FAZ A NAVEGAÇÃO CORRETA (Igual à do Menu)
    val navigateToProfile = {
        navController.navigate("profile") {
            // Isto evita que se criem várias cópias da mesma página no histórico
            popUpTo(navController.graph.findStartDestination().id) { 
                saveState = true 
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val currentBikeState = when(val state = uiState) {
        is HomeUiState.Success -> state.bikeState
        else -> BikeState()
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        drawerShape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                        drawerContainerColor = Color(0xFF121212),
                        modifier = Modifier.width(320.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 24.dp)
                        ) {
                            // Header: Back + Logo
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Gray)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Image(
                                    painter = painterResource(id = R.drawable.logo_app),
                                    contentDescription = "myFULGORA",
                                    modifier = Modifier.height(100.dp)
                                )
                                Spacer(modifier = Modifier.weight(1.2f))
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Menu Items
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

                            // Bottom Items
                            DrawerItem(
                                label = stringResource(R.string.navbar_help),
                                icon = AppIcons.Menu.Help,
                                onClick = {
                                    scope.launch { drawerState.close() }

                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://amover.utad.pt/")
                                    )
                                    context.startActivity(intent)
                                }
                            )
                            DrawerItem(
                                label = stringResource(R.string.navbar_logout),
                                icon = AppIcons.Menu.Logout,
                                onClick = { /* Logout */ }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = BlackBrand, contentColor = Color.Gray) {

                            data class NavItem(
                                val route: String,
                                val labelRes: Int,
                                val icon: Int
                            )

                            val navItems = listOf(
                                NavItem("map", R.string.navbar_map, AppIcons.Navbar.Map),
                                NavItem("battery", R.string.navbar_battery, AppIcons.Navbar.Battery),
                                NavItem("home", R.string.navbar_home, AppIcons.Navbar.Home),
                                NavItem("social", R.string.navbar_social, AppIcons.Navbar.Social),
                                NavItem("performance", R.string.navbar_performance, AppIcons.Navbar.Performance)
                            )

                            navItems.forEach { item ->
                                val isSelected = currentRoute == item.route

                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            painterResource(id = item.icon),
                                            contentDescription = stringResource(id = item.labelRes),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {
                                        Text(text = stringResource(id = item.labelRes))
                                    },
                                    selected = isSelected,
                                    alwaysShowLabel = false,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GreenFresh,
                                        selectedTextColor = GreenFresh,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
                        composable("map") {
                            MapScreen(
                                state = currentBikeState,
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onUserClick = { navigateToProfile() },
                                onCalendarClick = { navController.navigate("maintenance_screen") },
                                onViewAllClick = {
                                    navController.navigate("history")
                                }
                            )
                        }

                        // 2. O novo ecrã de Histórico
                        composable("history") {
                            TripHistoryScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("profile") { 
                            ProfileScreen(
                                navController = navController, 
                                onMenuClick = { scope.launch { drawerState.open() } }
                            ) 
                        }
                        composable("battery") { 
                            BatteryScreen(
                                state = currentBikeState,
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onUserClick = { navigateToProfile() },
                                onCalendarClick = { navController.navigate("maintenance_screen") }
                            ) 
                        }
                        composable("home") {
                            HomeScreen(
                                state = currentBikeState,
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onUserClick = { navigateToProfile() },
                                onCalendarClick = { navController.navigate("maintenance_screen") }
                            )
                        }
                        composable("maintenance_screen") {
                            MaintenanceScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("social") { 
                            SocialScreen(
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onUserClick = { navigateToProfile() },
                                onCalendarClick = { navController.navigate("maintenance_screen") }
                            ) 
                        }
                        composable("performance") { 
                            PerformanceScreen(
                                state = currentBikeState,
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onUserClick = { navigateToProfile() }
                            ) 
                        }
                        composable("delegation") { 
                            DelegationScreen(
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onUserClick = { navigateToProfile() }
                            ) 
                        }
                        composable("settings") { 
                            SettingsScreen(
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onUserClick = { navigateToProfile() }
                            )
                        }
                        composable("documentation") { 
                            DocumentationScreen(
                                state = currentBikeState, // Passas a bandeja de dados
                                onSaveDocument = { nome, uri ->
                                    viewModel.guardarDocumento(nome, uri)
                                }
                            ) 
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(
    label: String,
    icon: Any,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val color = if (selected) GreenFresh else Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 18.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
        Spacer(modifier = Modifier.width(16.dp))
        when (icon) {
            is ImageVector -> Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            is Int -> Icon(painterResource(id = icon), null, tint = color, modifier = Modifier.size(24.dp))
        }
    }
}
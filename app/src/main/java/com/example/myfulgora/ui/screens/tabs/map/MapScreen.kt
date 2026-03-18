package com.example.myfulgora.ui.screens.tabs.map

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.R
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.helpers.SettingsManager
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.components.RecentTripRow
import com.example.myfulgora.ui.theme.DarkTextSecondary
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onViewAllClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val isMetric by settingsManager.isMetricFlow.collectAsState(initial = true)
    val currentUser = UserManager.currentUser

    val bikeLocation = LatLng(-20.310380, -40.294931)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bikeLocation, 15f)
    }

    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 32.dp,
        sheetShape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
        sheetContainerColor = Color(0xFF1A1A1A).copy(alpha = 0.95f),
        sheetContentColor = Color.White,
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
            )
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                FulgoraInfoCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(id = R.string.performance_recent_trips), color = DarkTextSecondary, fontSize = Dimens.TextSizeTitle)
                            Text(
                                stringResource(id = R.string.performance_view_all),
                                color = GreenFresh,
                                fontSize = Dimens.TextSizeSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onViewAllClick() }
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                        RecentTripRow(
                            date = "Today, 14:20",
                            route = "Home - Office",
                            distance = if (isMetric) "12 km" else "7 mi",
                            energy = "0.4 kWh"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.1f))

                        RecentTripRow(
                            date = "Yesterday, 18:30",
                            route = "Office - Gym",
                            distance = if (isMetric) "5 km" else "3 mi",
                            energy = "0.1 kWh"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    ) { innerPadding ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val screenW = this.maxWidth
            val iconSize = screenW * Dimens.IconScaleRatio
            val paddingSide = screenW * Dimens.SideMarginRatio

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapStyleOptions = MapStyleOptions(MapStyles.MapStyleDark)
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                )
            ) {
                Marker(
                    state = MarkerState(position = bikeLocation),
                    title = "My Fulgora",
                    snippet = "Parked 2h ago"
                )
            }

            // TOPBAR IDENTICA À BATERIA/PERFORMANCE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF000000).copy(alpha = 0.95f),
                                Color(0xFF000000).copy(alpha = 0.7f),
                                Color(0xFF000000).copy(alpha = 0.0f)
                            )
                        )
                    )
                    .padding(horizontal = paddingSide)
                    .padding(top = Dimens.TopPadding, bottom = 40.dp)
                    .align(Alignment.TopCenter)
            ) {
                FulgoraTopBar(
                    userName = currentUser?.profile?.name ?: "Rider",
                    iconSize = iconSize, // Agora usa o tamanho dinâmico igual aos outros
                    onMenuClick = onMenuClick,
                    onUserClick = onUserClick,
                    onCalendarClick = onCalendarClick
                )
            }

            FloatingActionButton(
                onClick = {
                    val uri = "geo:0,0?q=${bikeLocation.latitude},${bikeLocation.longitude}(Mota+Fulgora)"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.google.android.apps.maps")
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        context.startActivity(fallbackIntent)
                    }
                },
                containerColor = GreenFresh,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 48.dp)
            ) {
                Icon(Icons.Filled.Navigation, contentDescription = "Navegar para a mota")
            }
        }
    }
}
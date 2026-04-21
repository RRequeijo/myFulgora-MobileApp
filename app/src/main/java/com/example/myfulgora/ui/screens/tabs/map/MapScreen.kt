package com.example.myfulgora.ui.screens.tabs.map

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.model.BikeState
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    state: BikeState,
    onViewAllClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUser = UserManager.currentUser

    val bikeLocation = LatLng(state.latitude, state.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bikeLocation, 15f)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenW = LocalContext.current.resources.displayMetrics.widthPixels.dp / LocalContext.current.resources.displayMetrics.density
        val iconSize = screenW * Dimens.IconScaleRatio
        val paddingSide = screenW * Dimens.SideMarginRatio

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
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

        // Barra Superior com Gradiente
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
                iconSize = iconSize,
                onMenuClick = onMenuClick,
                onUserClick = onUserClick,
                onCalendarClick = onCalendarClick
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                // Ajusta este valor de 'bottom' para subir ou descer AMBOS de uma vez
                // 110.dp costuma ser o ideal para ficar mesmo acima da Bottom Bar
                .padding(end = 16.dp, bottom = 130.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp) // 👈 Espaçamento perfeito de 16dp entre os dois botões
        ) {

            // 1. Botão do Histórico (Fica por cima na Column)
            FloatingActionButton(
                onClick = { onViewAllClick() },
                containerColor = Color(0xFF1A1A1A),
                contentColor = GreenFresh
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Trip History",
                    modifier = Modifier.size(24.dp)
                )
            }

            // 2. Botão de Navegação (Fica por baixo na Column)
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
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Navigation, contentDescription = "Navigate to Bike")
            }
        }
    }
}

package com.example.myfulgora.ui.screens.tabs.map

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.model.BikeState
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    state: BikeState,
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

    // Ícone da mota redimensionado para o mapa (aprox. 40-50dp)
    val bikeIcon = remember {
        MapsInitializer.initialize(context)
        val bitmap = BitmapFactory.decodeResource(context.resources, com.example.myfulgora.R.drawable.mota_crop)
        val density = context.resources.displayMetrics.density
        val targetWidth = (40 * density).toInt() // 40dp em pixels
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val targetHeight = (targetWidth / aspectRatio).toInt()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
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
                snippet = "Parked 2h ago",
                icon = bikeIcon,
                anchor = androidx.compose.ui.geometry.Offset(0.5f, 0.5f)
            )
        }

        // Barra Superior com Gradiente Adaptativo
        val fadeColor = MaterialTheme.colorScheme.background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            fadeColor.copy(alpha = 0.95f),
                            fadeColor.copy(alpha = 0.7f),
                            fadeColor.copy(alpha = 0.0f)
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

        // Botão de Navegação
        ExtendedFloatingActionButton(
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
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 130.dp),
            containerColor = GreenFresh,
            contentColor = Color.White,
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            text = {
                Text(
                    text = "Como chegar?",
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}

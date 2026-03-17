package com.example.myfulgora.ui.screens.tabs.map

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.R
import com.example.myfulgora.ui.components.FulgoraInfoCard // Garante que os imports estão corretos
import com.example.myfulgora.ui.screens.tabs.map.MapStyles
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GrayLight
import com.example.myfulgora.ui.theme.GreenFresh
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.example.myfulgora.ui.components.RecentTripRow // Verifica se o import está correto
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Navigation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
        onViewAllClick: () -> Unit = {}
    ) {
        val context = LocalContext.current
        val bikeLocation = LatLng(-20.310380,-40.294931)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(bikeLocation, 15f)
        }

        val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        // Altura da gaveta quando fechada (podes ajustar para mostrar mais ou menos da borda)
        sheetPeekHeight = 60.dp,
        sheetContainerColor = Color(0xFF1A1A1A), // Cor de fundo da gaveta (escura)
        sheetContentColor = Color.White,

        // 1. O CONTEÚDO DA GAVETA (As tuas viagens!)
        sheetContent = {
            // O pequeno traço cinzento no topo (drag handle) é automático do Material3
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // AQUI ENTRA O TEU CÓDIGO EXATO:
                FulgoraInfoCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(id = R.string.performance_recent_trips), color = GrayLight, fontSize = Dimens.TextSizeTitle)
                            Text(
                                stringResource(id = R.string.performance_view_all),
                                color = GreenFresh,
                                fontSize = Dimens.TextSizeSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    onViewAllClick()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                        RecentTripRow(
                            date = "Today, 14:20",
                            route = "Home - Office",
                            distance = "12 km",
                            energy = "0.4 kWh"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.1f))

                        RecentTripRow(
                            date = "Yesterday, 18:30",
                            route = "Office - Gym",
                            distance = "5 km",
                            energy = "0.1 kWh"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.1f))

                        RecentTripRow(
                            date = "12 Oct, 09:00",
                            route = "Weekend Ride",
                            distance = "45 km",
                            energy = "1.2 kWh"
                        )
                    }
                }

                // Espaço extra no fundo para não colar os cartões ao fim do ecrã
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    ) { innerPadding ->

        // 2. O MAPA E O BOTÃO FLUTUANTE
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    // Lemos o estilo que guardámos no outro ficheiro!
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

            // Botão flutuante "Navegar para a mota"
            FloatingActionButton(
                onClick = {
                    // Cria o link (URI) com as coordenadas da mota e um nome (Mota Fulgora)
                    val uri = "geo:0,0?q=${bikeLocation.latitude},${bikeLocation.longitude}(Mota+Fulgora)"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

                    // Força a abrir especificamente no Google Maps
                    intent.setPackage("com.google.android.apps.maps")

                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Caso o utilizador não tenha o Google Maps instalado, tenta abrir com qualquer outro GPS (ex: Waze, Petal Maps)
                        val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        context.startActivity(fallbackIntent)
                    }
                },
                containerColor = GreenFresh,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 40.dp) // Mantém o padding para não bater na gaveta
            ) {
                // Mudei o ícone para Navigation (uma setinha de GPS)
                Icon(Icons.Filled.Navigation, contentDescription = "Navegar para a mota")
            }
        }
    }
}




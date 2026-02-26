package com.example.myfulgora.ui.screens.tabs.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import com.example.myfulgora.ui.theme.White
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage

private enum class SupportDialogType {
    Assistance,
    DealershipContact
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    navController: NavController,
    onMenuClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val currentBike = UserManager.getCurrentBike()

    // Valores seguros (Fallback)
    val bikeName = currentBike?.name ?: "No Motorcycle"
    val bikeVin = currentBike?.vin ?: "---"
    val isConnected = currentBike?.isConnected ?: false

    // Estados do Popup
    var showDialog by remember { mutableStateOf(false) }
    var campoAEditar by remember { mutableStateOf("") }
    var valorTemporario by remember { mutableStateOf("") }
    var showSupportDialog by remember { mutableStateOf(false) }
    var supportDialogType by remember { mutableStateOf<SupportDialogType?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let {
                try {
                    // 1. Pedir permissão persistente para a URI
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    viewModel.atualizarFoto(it.toString())
                } catch (e: Exception) {
                    // Se falhar (ex: a galeria não suporta persistência), guardamos apenas a URI
                    viewModel.atualizarFoto(it.toString())
                }
            }
        }
    )

    FulgoraBackground {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val screenW = this.maxWidth
            val iconSize = screenW * Dimens.IconScaleRatio
            val paddingSide = screenW * Dimens.SideMarginRatio

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = paddingSide)
                    .padding(top = Dimens.TopPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FulgoraTopBar(
                    iconSize = iconSize,
                    onMenuClick = onMenuClick
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Profile",
                        fontSize = Dimens.TextSizeHeader,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.photoUri != null) {
                            AsyncImage(
                                model = state.photoUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = AppIcons.Profile.profile),
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = state.name, color = White, fontSize = 24.sp)
                        Text(text = state.email, color = Color.Gray.copy(alpha = 0.7f), fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = bikeName, color = Color.White, fontSize = 20.sp, style = MaterialTheme.typography.titleLarge)
                            Text(text = if (isConnected) "Connected" else "Offline", color = if (isConnected) GreenFresh else Color.Red.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text(text = "VIN: $bikeVin", color = Color.Gray.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                        Image(painter = painterResource(id = AppIcons.Dashboard.MainBike), contentDescription = null, modifier = Modifier.size(80.dp))
                        Icon(painter = painterResource(id = AppIcons.Dashboard.ArrowRight0), contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp).clickable { navController.navigate("documentation") })
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- Add/Remove Motorcycle (GARAGEM) ---
                FulgoraInfoCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !state.isSyncing) { // 👈 Só clica se não estiver a carregar
                                viewModel.sincronizarNovaMota()
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Sync New Motorcycle",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            // Se tiver motas, diz quantas tem na garagem
                            if (state.totalBikes > 0) {
                                Text(
                                    text = "${state.totalBikes} bikes in garage",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                            // Mensagem de sucesso bonita a verde
                            if (state.showSyncSuccess) {
                                Text(
                                    text = "Sync successful! New bike added.",
                                    color = GreenFresh,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Mostra a rodinha a girar ou a seta normal
                        if (state.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = GreenFresh,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // ACCOUNT

                FulgoraInfoCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Account", color = GreenFresh, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                        val accountItems = listOf("Edit Name", "Edit Email", "Change Password")
                        accountItems.forEachIndexed { index, item ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { campoAEditar = item; valorTemporario = ""; showDialog = true }.padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = item, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                Icon(painter = painterResource(id = AppIcons.Dashboard.ArrowRight0), contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            }
                            if (index < accountItems.size - 1) HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // --- Support Section ---
                FulgoraInfoCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Support", color = GreenFresh, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                        
                        // Assistance
                        Row(modifier = Modifier.fillMaxWidth().clickable { 
                            supportDialogType = SupportDialogType.Assistance
                            showSupportDialog = true 
                        }.padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Assistance", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Icon(painter = painterResource(id = AppIcons.Dashboard.ArrowRight0), contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                        
                        // Dealership Contact
                        Row(modifier = Modifier.fillMaxWidth().clickable { 
                            supportDialogType = SupportDialogType.DealershipContact
                            showSupportDialog = true 
                        }.padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Dealership Contact", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Icon(painter = painterResource(id = AppIcons.Dashboard.ArrowRight0), contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.ScrollBottomPadding))
            }

            // Edit Dialog
            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    FulgoraInfoCard {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = campoAEditar, color = GreenFresh, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            TextField(
                                value = valorTemporario,
                                onValueChange = { valorTemporario = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text(text = when(campoAEditar) { "Edit Name" -> "Enter new name..."; "Edit Email" -> "Enter new email..."; else -> "Type here..." }, color = Color.Gray) },
                                colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF2A2A2A), unfocusedContainerColor = Color(0xFF2A2A2A), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { showDialog = false }) { Text("Cancel", color = Color.Gray) }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { viewModel.atualizarDado(campoAEditar, valorTemporario); showDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = GreenFresh)) { Text("Save", color = Color.Black) }
                            }
                        }
                    }
                }
            }

            // Support Dialog
            if (showSupportDialog && supportDialogType != null) {
                Dialog(
                    onDismissRequest = {
                        showSupportDialog = false
                        supportDialogType = null
                    }
                ) {
                    FulgoraInfoCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            when (supportDialogType) {
                                SupportDialogType.Assistance -> {
                                    Text(
                                        text = "Assistance",
                                        color = GreenFresh,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Need help with your Fulgora? You can reach our assistance team through the following contacts:",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // 👇 TELEFONE CLICÁVEL
                                    Text(
                                        text = "Phone:",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "+351 912 345 678",
                                        color = White,
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+351912345678"))
                                                context.startActivity(intent)
                                            }
                                    )

                                    // 👇 EMAIL CLICÁVEL
                                    Text(
                                        text = "Email:",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "dealer@fulgora.pt",
                                        color = White,
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:dealer@fulgora.pt"))
                                                context.startActivity(intent)
                                            }
                                    )
                                }

                                SupportDialogType.DealershipContact -> {
                                    Text(
                                        text = "Fulgora Mobility, Lda.",
                                        color = GreenFresh,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // 👇 TELEFONE CLICÁVEL
                                    Text(
                                        text = "Phone:",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "+351 912 345 678",
                                        color = White,
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+351912345678"))
                                                context.startActivity(intent)
                                            }
                                    )

                                    // 👇 EMAIL CLICÁVEL
                                    Text(
                                        text = "Email:",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "dealer@fulgora.pt",
                                        color = White,
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:dealer@fulgora.pt"))
                                                context.startActivity(intent)
                                            }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Address:",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )

                                    // 👇 MORADA CLICÁVEL (Abre o Google Maps)
                                    Text(
                                        text = "Rua da Mobilidade 123\n4000-000 Porto, Portugal",
                                        color = White,
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                val uri = "geo:0,0?q=Rua da Mobilidade 123, 4000-000 Porto, Portugal"
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                                intent.setPackage("com.google.android.apps.maps")
                                                if (intent.resolveActivity(context.packageManager) != null) {
                                                    context.startActivity(intent)
                                                }
                                            }
                                    )
                                }

                                null -> {}
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        showSupportDialog = false
                                        supportDialogType = null
                                    }
                                ) {
                                    Text("Close", color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

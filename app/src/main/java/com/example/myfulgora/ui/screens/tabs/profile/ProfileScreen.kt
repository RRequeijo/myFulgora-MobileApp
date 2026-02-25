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
    val currentUser = UserManager.currentUser
    val context = LocalContext.current

    // Valores seguros (Fallback)
    val bikeName = currentUser?.bike?.name ?: "No Motorcycle"
    val bikeVin = currentUser?.bike?.vin ?: "---"
    val isConnected = currentUser?.bike?.isConnected ?: false

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

                FulgoraInfoCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Add/Remove Motorcycle", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Icon(painter = painterResource(id = AppIcons.Dashboard.ArrowRight0), contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

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
            if (showSupportDialog) {
                Dialog(onDismissRequest = { showSupportDialog = false }) {
                    FulgoraInfoCard {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            when (supportDialogType) {
                                SupportDialogType.Assistance -> {
                                    Text("Assistance", color = GreenFresh, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Need help with your bike?\nOur technical team is available 24/7.", color = Color.White, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        onClick = { val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:+351912345678") }; context.startActivity(intent) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = GreenFresh)
                                    ) { Text("Call Support", color = Color.Black) }
                                }
                                SupportDialogType.DealershipContact -> {
                                    Text("Dealership Contact", color = GreenFresh, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Fulgora Motors HQ", color = Color.White, fontSize = 16.sp, textDecoration = TextDecoration.Underline)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Phone: +351 210 000 000", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                    Text("Email: contact@fulgora.com", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                    Text("Address: Av. da Liberdade, Lisboa", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(onClick = { showSupportDialog = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = GreenFresh)) { Text("Close", color = Color.Black) }
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}

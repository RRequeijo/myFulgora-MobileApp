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

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    navController: NavController,
    onMenuClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val currentUser = UserManager.currentUser

    val userName = currentUser?.profile?.name ?: "Unknown Rider"
    val userEmail = currentUser?.profile?.email ?: "No Email"
    val bikeName = currentUser?.bike?.name ?: "No Motorcycle"
    val bikeVin = currentUser?.bike?.vin ?: "---"
    val isConnected = currentUser?.bike?.isConnected ?: false

    FulgoraBackground {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        )
        {
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

                // --- 3. DADOS PESSOAIS ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = AppIcons.Profile.profile),
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(90.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.name, // 👈 Substitui "John Doe"
                            color = White,
                            fontSize = 24.sp
                        )
                        Text(
                            text = state.email, // 👈 Substitui "johndoe@gmail..."
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // --- 4. DADOS DA MOTA ---
                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bikeName, // 👈 Usa o nome da mota
                                color = Color.White,
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = if (isConnected) "Connected" else "Offline", // 👈 Dinâmico
                                color = if (isConnected) GreenFresh else Color.Red.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "VIN: $bikeVin", // 👈 Usa o VIN da mota
                                color = Color.Gray.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                        Image(
                            painter = painterResource(id = AppIcons.Dashboard.MainBike),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                        Icon(
                            painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    navController.navigate("documentation")
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Adicionar/Remover Mota
                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Add/Remove Motorcycle",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Icon(
                            painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // Account Settings
                FulgoraInfoCard {
                    // 👇 Envolver tudo numa Column resolve erros de layout no Card
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Account",
                            color = GreenFresh,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                        val accountItems = listOf("Edit Name", "Edit Email", "Change Password")
                        accountItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = item, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                Icon(
                                    painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (index < accountItems.size - 1) {
                                HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // Support
                FulgoraInfoCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Support",
                            color = GreenFresh,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                        val supportItems = listOf("Assistance", "Dealership Contact", "More to Come")
                        supportItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = item, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                Icon(
                                    painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (index < supportItems.size - 1) {
                                HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.ScrollBottomPadding))
            }
        }
    }
}
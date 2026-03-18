package com.example.myfulgora.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.R
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun DelegationScreen(
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
) {
    val currentUser = UserManager.currentUser
    val scrollState = rememberScrollState()
    
    // Estados para os inputs
    var inviteEmail by remember { mutableStateOf("") }
    var activationCode by remember { mutableStateOf("") }

    FulgoraBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenW = this.maxWidth
            val iconSize = screenW * Dimens.IconScaleRatio
            val paddingSide = screenW * Dimens.SideMarginRatio

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = paddingSide)
                    .padding(top = Dimens.TopPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. TOP BAR
                FulgoraTopBar(
                    userName = currentUser?.profile?.name ?: "Rider",
                    iconSize = iconSize,
                    onMenuClick = onMenuClick,
                    onUserClick = onUserClick,
                    onCalendarClick = onCalendarClick
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                // 2. TÍTULO
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.delegation_title),
                        fontSize = Dimens.TextSizeHeader,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(id = R.string.delegation_subtitle),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                // 3. CARTÃO: GERAR NOVA CHAVE (Para o Dono)
                FulgoraInfoCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddLink, contentDescription = null, tint = GreenFresh)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.delegation_share_motorcycle_title),
                            color = GreenFresh,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                    Text(
                        text = stringResource(id = R.string.delegation_share_motorcycle_description),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inviteEmail,
                        onValueChange = { inviteEmail = it },
                        label = { Text(stringResource(id = R.string.delegation_guest_email), color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenFresh,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* Lógica gRPC: AssignGuestPermission */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenFresh),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(id = R.string.delegation_generate_key), color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // 4. CARTÃO: ATIVAR CHAVE (Para o Convidado)
                FulgoraInfoCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = GreenFresh)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.delegation_activate_key_title),
                            color = GreenFresh,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                    OutlinedTextField(
                        value = activationCode,
                        onValueChange = { activationCode = it },
                        label = { Text(stringResource(id = R.string.delegation_activation_code), color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenFresh,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* Lógica: Validar Token no Servidor */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(id = R.string.delegation_activate_access), color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // 5. LISTA DE ACESSOS ATIVOS (Opcional, só para o dono ver)
                Text(
                    text = stringResource(id = R.string.delegation_active_accesses),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                FulgoraInfoCard {
                    // Exemplo de um acesso ativo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("convidado@email.com", color = Color.White, fontSize = 14.sp)
                            Text("Ativo até: Indeterminado", color = Color.Gray, fontSize = 12.sp)
                        }
                        IconButton(onClick = { /* Lógica gRPC: RevokeGuestAccess */ }) {
                            Icon(Icons.Default.Delete, contentDescription = "Revogar", tint = Color.Red.copy(alpha = 0.7f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.ScrollBottomPadding))
            }
        }
    }
}

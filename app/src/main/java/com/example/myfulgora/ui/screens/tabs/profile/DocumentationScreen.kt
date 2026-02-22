package com.example.myfulgora.ui.screens.tabs.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column // 👈 Import adicionado
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding // 👈 Import adicionado
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenDeep

@Composable
fun DocumentationScreen(
    onMenuClick: () -> Unit = {}
){
    FulgoraBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize())
        {
            val screenW = this.maxWidth
            val iconSize = screenW * Dimens.IconScaleRatio

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.SideMarginRatio * screenW) // Para não ficar colado aos cantos
                    .padding(top = Dimens.TopPadding) // Para não ficar debaixo da barra de estado (relógio/bateria do telemóvel)
            ){
                FulgoraTopBar(
                    iconSize = iconSize,
                    onMenuClick = onMenuClick
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Documento Único Automóvel (DUA)",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Open",
                            color = GreenDeep,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Certificado de Seguro",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Open",
                            color = GreenDeep,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ficha de Inspeção Periódica",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Open",
                            color = GreenDeep,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Manual da Mota",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Open",
                            color = GreenDeep,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Histórico de Manutenção / Livro de Revisões",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Open",
                            color = GreenDeep,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Contratos/Orçamentos de Reparações",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Open",
                            color = GreenDeep,
                            fontSize = 14.sp
                        )
                    }
                }

            }
        }
    }
}
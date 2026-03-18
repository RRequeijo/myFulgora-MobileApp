package com.example.myfulgora.ui.screens.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.R
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import com.example.myfulgora.data.auth.UserManager

@Composable
fun SocialScreen(
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
) {
    val currentUser = UserManager.currentUser

    FulgoraBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenW = maxWidth
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
                // 1. TOP BAR
                FulgoraTopBar(
                    userName = currentUser?.profile?.name ?: "Rider",
                    iconSize = iconSize,
                    onMenuClick = onMenuClick,
                    onUserClick = onUserClick,
                    onCalendarClick = onCalendarClick
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                // 2. HEADER TEXT
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.social_title),
                        fontSize = Dimens.TextSizeHeader,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(id = R.string.social_subtitle),
                        fontSize = Dimens.TextSizeNormal,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                // 3. E-POINTS CARD
                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = AppIcons.Social.epoints),
                            contentDescription = null,
                            tint = GreenFresh,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "1,240",
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(id = R.string.social_epoints),
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            Text(
                                text = stringResource(id = R.string.social_epoints_description),
                                color = Color.Gray.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // 4. TOP RIDER CARD
                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = AppIcons.Social.top_rider),
                                contentDescription = null,
                                tint = GreenFresh,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = R.string.social_top_rider),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(id = R.string.social_top_rider_description),
                                color = Color.Gray,
                                fontSize = 12.sp,
                                lineHeight = 14.sp
                            )
                        }
                        Icon(
                            painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                // 5. REWARDS LIST
                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.social_rewards),
                            color = GreenFresh,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            painter = painterResource(id = AppIcons.Social.rewards),
                            contentDescription = null,
                            tint = GreenFresh,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                    val rewards = listOf(stringResource(id = R.string.social_maintence_discounts), stringResource(id = R.string.social_free_accessories), stringResource(id = R.string.social_eco_badge), stringResource(id = R.string.social_more_to_come))
                    rewards.forEachIndexed { index, reward ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = reward, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Icon(
                                painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (index < rewards.size - 1) {
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // 6. JOIN COMMUNITY BUTTON
                FulgoraInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.social_join_community),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            painter = painterResource(id = AppIcons.Dashboard.ArrowRight0),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.ScrollBottomPadding))
            }
        }
    }
}
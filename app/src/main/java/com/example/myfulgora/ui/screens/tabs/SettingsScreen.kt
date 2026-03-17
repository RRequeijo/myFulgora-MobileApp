package com.example.myfulgora.ui.screens.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfulgora.ui.components.FulgoraBackground
import com.example.myfulgora.ui.components.FulgoraInfoCard
import com.example.myfulgora.ui.components.FulgoraTopBar
import com.example.myfulgora.ui.theme.AppIcons
import com.example.myfulgora.ui.theme.Dimens
import com.example.myfulgora.ui.theme.GreenFresh
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.filled.Check
import androidx.core.os.LocaleListCompat
import androidx.compose.ui.res.stringResource
import com.example.myfulgora.R
import androidx.compose.ui.draw.rotate
import com.example.myfulgora.data.auth.UserManager
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.myfulgora.data.helpers.SettingsManager

@Composable
fun SettingsScreen(
    onMenuClick: () -> Unit = {},
    onUserClick: () -> Unit = {}
) {
    // 1. Pega no contexto e cria o gestor
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    // 2. O Scope para podermos guardar os dados (suspend functions)
    val coroutineScope = rememberCoroutineScope()
    // 3. Lê o valor real da memória! (O collectAsState transforma o Flow num estado para o Compose ler)
    val isMetric by settingsManager.isMetricFlow.collectAsState(initial = true)
    var notificationsEnabled by remember { mutableStateOf(true) }
    var lowBatteryAlertEnabled by remember { mutableStateOf(true) }


    FulgoraBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
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
                // 1. TOP BAR
                val currentUser = UserManager.currentUser
                FulgoraTopBar(
                    userName = currentUser?.profile?.name ?: "Rider",
                    iconSize = iconSize,
                    onMenuClick = onMenuClick,
                    onUserClick = onUserClick
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                // 2. HEADER TEXT
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.settings_title),
                        fontSize = Dimens.TextSizeHeader,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                // 6. PREFERENCES
                FulgoraInfoCard {
                    Text(
                        text = stringResource(id = R.string.settings_preferences),
                        color = GreenFresh,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                    // Units Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Units", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        
                        // Custom Unit Switcher
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2D2D2D))
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(if (isMetric) GreenFresh else Color.Transparent)
                                    .clickable {
                                        coroutineScope.launch { settingsManager.saveIsMetric(true) }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "km",
                                    color = if (isMetric) Color.Black else Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(if (!isMetric) GreenFresh else Color.Transparent)
                                    .clickable {
                                        coroutineScope.launch { settingsManager.saveIsMetric(false) }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.settings_units_miles),
                                    color = if (!isMetric) Color.Black else Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

                    // Notifications Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.settings_notifications), color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Switch(
                            modifier = Modifier.scale(0.85f),
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = GreenFresh,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color(0xFF2D2D2D),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }

                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

                    // Low battery Alert Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.settings_low_battery), color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Switch(
                            modifier = Modifier.scale(0.85f),
                            checked = lowBatteryAlertEnabled,
                            onCheckedChange = { lowBatteryAlertEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = GreenFresh,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color(0xFF2D2D2D),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // 7. STYLES
                FulgoraInfoCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.settings_style),
                            color = GreenFresh,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                        // --- ITEM 1: LANGUAGE (COM DROPDOWN) ---
                        LanguageSelectorRow()

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

                        // --- ITEM 2: THEME (Ainda estático por enquanto) ---
                        ThemeSelectorRow()
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.ScrollBottomPadding))
            }
        }
    }
}

@Composable
fun LanguageSelectorRow() {
    var expanded by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ArrowRotation"
    )

    val languages = mapOf(
        "English" to "en",
        "Português" to "pt",
        "Chinese" to "zh"
    )

    val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    val displayLanguage = when {
        currentLocale.contains("pt") -> "Português"
        currentLocale.contains("zh") -> "Chinese"
        else -> "English"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.settings_language),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Text(
                text = displayLanguage,
                color = GreenFresh,
                fontSize = 12.sp
            )

            Icon(
                painter = painterResource(id = AppIcons.Actions.DropDown),
                contentDescription = null,
                tint = if (expanded) GreenFresh else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationState)
            )
        }

        if (expanded) {
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                languages.forEach { (name, code) ->
                    val isSelected = name == displayLanguage

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                changeAppLanguage(code)
                                expanded = false
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            color = if (isSelected) GreenFresh else Color.White,
                            fontSize = 14.sp
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = GreenFresh,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun changeAppLanguage(languageCode: String) {
    val appLocale = LocaleListCompat.forLanguageTags(languageCode)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

@Composable
fun ThemeSelectorRow() {
    var expanded by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ArrowRotation"
    )

    val themeOptions = listOf(
        "System Default" to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
        "Light Mode" to AppCompatDelegate.MODE_NIGHT_NO,
        "Dark Mode" to AppCompatDelegate.MODE_NIGHT_YES
    )

    val currentMode = AppCompatDelegate.getDefaultNightMode()

    val displayTheme = when (currentMode) {
        AppCompatDelegate.MODE_NIGHT_NO -> "Light Mode"
        AppCompatDelegate.MODE_NIGHT_YES -> "Dark Mode"
        else -> "System Default"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Theme",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Text(
                text = displayTheme,
                color = GreenFresh,
                fontSize = 12.sp
            )

            Icon(
                painter = painterResource(id = AppIcons.Actions.DropDown),
                contentDescription = null,
                tint = if (expanded) GreenFresh else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationState)
            )
        }

        if (expanded) {
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                themeOptions.forEach { (name, mode) ->
                    val isSelected = name == displayTheme

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                AppCompatDelegate.setDefaultNightMode(mode)
                                expanded = false
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            color = if (isSelected) GreenFresh else Color.White,
                            fontSize = 14.sp
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = GreenFresh,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
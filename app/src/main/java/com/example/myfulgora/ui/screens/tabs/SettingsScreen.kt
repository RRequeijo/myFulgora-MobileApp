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
    onUserClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
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
    val bottomNavHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val primaryColor = MaterialTheme.colorScheme.primary


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
                FulgoraTopBar(
                    iconSize = iconSize,
                    onMenuClick = onMenuClick,
                    onUserClick = onUserClick,
                    onCalendarClick = onCalendarClick
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                // 2. HEADER TEXT
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.settings_title),
                        fontSize = Dimens.TextSizeHeader,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                // 6. PREFERENCES
                FulgoraInfoCard {
                    Text(
                        text = stringResource(id = R.string.settings_preferences),
                        color = primaryColor,
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
                        Text(text = stringResource(id = R.string.settings_units), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), fontSize = 14.sp)
                        
                        // Custom Unit Switcher
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(if (isMetric) primaryColor else Color.Transparent)
                                    .clickable {
                                        coroutineScope.launch { settingsManager.saveIsMetric(true) }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.settings_units_km),
                                    color = if (isMetric) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(if (!isMetric) primaryColor else Color.Transparent)
                                    .clickable {
                                        coroutineScope.launch { settingsManager.saveIsMetric(false) }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.settings_units_miles),
                                    color = if (!isMetric) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                    // Notifications Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.settings_notifications), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), fontSize = 14.sp)
                        Switch(
                            modifier = Modifier.scale(0.85f),
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = primaryColor,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                    // Low battery Alert Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.settings_low_battery), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), fontSize = 14.sp)
                        Switch(
                            modifier = Modifier.scale(0.85f),
                            checked = lowBatteryAlertEnabled,
                            onCheckedChange = { lowBatteryAlertEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = primaryColor,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
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
                            color = primaryColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                        // --- ITEM 1: LANGUAGE (COM DROPDOWN) ---
                        LanguageSelectorRow()

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        // --- ITEM 2: THEME (Ainda estático por enquanto) ---
                        ThemeSelectorRow()
                    }
                }

                Spacer(modifier = Modifier.height(100.dp + bottomNavHeight))
            }
        }
    }
}

@Composable
fun LanguageSelectorRow() {
    var expanded by remember { mutableStateOf(false) }
    val primaryColor = MaterialTheme.colorScheme.primary

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ArrowRotation"
    )

    val languages = mapOf(
        "English" to "en",
        "Português" to "pt"
    )

    val currentLocaleTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    
    // BUG FIX: Se estiver vazio, significa que está a usar o idioma do sistema.
    // Precisamos de detetar qual é o idioma atual real para mostrar o nome correto no seletor.
    val displayLanguage = remember(currentLocaleTags) {
        if (currentLocaleTags.isEmpty() || currentLocaleTags == "und") {
            // Se for "und" ou vazio, vemos o que o sistema está a usar agora
            val systemLocale = java.util.Locale.getDefault().language
            if (systemLocale.startsWith("pt")) "Português" else "English"
        } else {
            if (currentLocaleTags.contains("pt")) "Português" else "English"
        }
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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Text(
                text = displayLanguage,
                color = primaryColor,
                fontSize = 12.sp
            )

            Icon(
                painter = painterResource(id = AppIcons.Actions.DropDown),
                contentDescription = null,
                tint = if (expanded) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationState)
            )
        }

        if (expanded) {
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

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
                            color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = primaryColor,
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
    val primaryColor = MaterialTheme.colorScheme.primary

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ArrowRotation"
    )

    val themeOptions = listOf(
        stringResource(R.string.settings_theme_system) to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
        stringResource(R.string.settings_theme_light) to AppCompatDelegate.MODE_NIGHT_NO,
        stringResource(R.string.settings_theme_dark) to AppCompatDelegate.MODE_NIGHT_YES
    )

    val currentMode = AppCompatDelegate.getDefaultNightMode()

    val displayTheme = when (currentMode) {
        AppCompatDelegate.MODE_NIGHT_NO -> stringResource(R.string.settings_theme_light)
        AppCompatDelegate.MODE_NIGHT_YES -> stringResource(R.string.settings_theme_dark)
        else -> stringResource(R.string.settings_theme_system)
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
                text = stringResource(id = R.string.settings_theme),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Text(
                text = displayTheme,
                color = primaryColor,
                fontSize = 12.sp
            )

            Icon(
                painter = painterResource(id = AppIcons.Actions.DropDown),
                contentDescription = null,
                tint = if (expanded) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationState)
            )
        }

        if (expanded) {
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

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
                            color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

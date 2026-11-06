package com.novastream.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastream.ai.data.model.ThemeMode
import com.novastream.ai.ui.components.GlassCard
import com.novastream.ai.ui.components.NovaPrimaryButton
import com.novastream.ai.ui.components.NovaSecondaryButton
import com.novastream.ai.ui.theme.*
import com.novastream.ai.ui.viewmodel.NovaViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var backupJsonText by remember { mutableStateOf("") }
    var restoreFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var showBackupSection by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Theme Selection Card
        item {
            GlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "THEME & VISUAL IDENTITY",
                        color = ElectricOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val themes = listOf(
                        Pair(ThemeMode.DARK_GRAPHITE, "Dark Graphite"),
                        Pair(ThemeMode.CYBER_VIOLET, "Cyber Violet"),
                        Pair(ThemeMode.PURE_BLACK, "Pure Black (OLED)"),
                        Pair(ThemeMode.LIGHT, "Light Mode")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        themes.take(2).forEach { (mode, name) ->
                            val isSelected = settings.themeMode == mode
                            ThemeOptionBox(
                                name = name,
                                isSelected = isSelected,
                                onClick = { viewModel.updateSettings(settings.copy(themeMode = mode)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        themes.drop(2).forEach { (mode, name) ->
                            val isSelected = settings.themeMode == mode
                            ThemeOptionBox(
                                name = name,
                                isSelected = isSelected,
                                onClick = { viewModel.updateSettings(settings.copy(themeMode = mode)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Language & Localization Card
        item {
            GlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "LANGUAGE & LOCALIZATION",
                        color = NeonViolet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val languages = listOf(
                        Pair("en", "English"),
                        Pair("ar", "العربية (Morocco / Darija)"),
                        Pair("fr", "Français")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.forEach { (code, name) ->
                            val isSelected = settings.language == code
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonViolet else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { viewModel.updateSettings(settings.copy(language = code)) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stream Engine & Buffering Presets
        item {
            GlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "STREAM BUFFER & TIMEOUT PRESETS",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Connection Timeout: ${settings.streamTimeoutSeconds} seconds",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15, 30).forEach { timeout ->
                            val isSelected = settings.streamTimeoutSeconds == timeout
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ElectricOrange else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { viewModel.updateSettings(settings.copy(streamTimeoutSeconds = timeout)) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${timeout}s",
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hardware Acceleration",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Enables hardware GPU decoder for 4K and HLS playback",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = settings.hwAcceleration,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(hwAcceleration = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = ElectricOrange)
                        )
                    }
                }
            }
        }

        // Backup and Restore (JSON)
        item {
            GlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PORTABLE JSON BACKUP & RESTORE",
                            color = ElectricOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (showBackupSection) "Hide" else "Manage",
                            color = NeonViolet,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showBackupSection = !showBackupSection }
                        )
                    }

                    if (showBackupSection) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NovaPrimaryButton(
                                text = "Export JSON Backup",
                                onClick = {
                                    coroutineScope.launch {
                                        backupJsonText = viewModel.exportBackupJson()
                                        restoreFeedbackMessage = "Backup generated (${backupJsonText.length} bytes)."
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            NovaSecondaryButton(
                                text = "Restore from JSON",
                                onClick = {
                                    if (backupJsonText.isNotBlank()) {
                                        viewModel.restoreBackup(backupJsonText) { success, msg ->
                                            restoreFeedbackMessage = msg
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        NovaInputField(
                            value = backupJsonText,
                            onValueChange = { backupJsonText = it },
                            placeholder = "Exported JSON will appear here, or paste backup JSON to restore...",
                            minLines = 6
                        )

                        if (restoreFeedbackMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = restoreFeedbackMessage!!,
                                color = if (restoreFeedbackMessage!!.startsWith("Restore failed")) StatusCrimson else StatusEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Maintenance & Reset
        item {
            GlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DATA MAINTENANCE & STORAGE",
                        color = StatusCrimson,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NovaSecondaryButton(
                            text = "Clear Viewing History",
                            onClick = { viewModel.clearViewingHistory() },
                            borderColor = StatusCrimson,
                            modifier = Modifier.weight(1f)
                        )
                        NovaSecondaryButton(
                            text = "Factory Reset Database",
                            onClick = { viewModel.resetAllData() },
                            borderColor = StatusCrimson,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ThemeOptionBox(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ElectricOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (isSelected) ElectricOrange.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = if (isSelected) ElectricOrange else MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

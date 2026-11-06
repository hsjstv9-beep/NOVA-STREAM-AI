package com.novastream.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastream.ai.data.model.Screen
import com.novastream.ai.ui.components.GlassCard
import com.novastream.ai.ui.components.NovaPrimaryButton
import com.novastream.ai.ui.components.NovaSecondaryButton
import com.novastream.ai.ui.theme.ElectricOrange
import com.novastream.ai.ui.theme.NeonViolet
import com.novastream.ai.ui.theme.StatusCrimson
import com.novastream.ai.ui.theme.StatusEmerald
import com.novastream.ai.ui.viewmodel.NovaViewModel

@Composable
fun PlaylistManagerScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val playlists by viewModel.playlists.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()
    val importMessage by viewModel.importMessage.collectAsState()
    val fixedCombos by viewModel.fixedCombos.collectAsState()
    val fixedErrorsCount by viewModel.fixedErrorsCount.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Playlists, 1: Import, 2: Combo Editor

    var importTitle by remember { mutableStateOf("") }
    var importUrl by remember { mutableStateOf("") }
    var importContent by remember { mutableStateOf("") }
    var importIsUrlMode by remember { mutableStateOf(true) }

    var rawComboText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Tab Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf("Playlists (${playlists.size})", "Import M3U", "Combo Editor")
            tabs.forEachIndexed { index, title ->
                val isSelected = activeTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) ElectricOrange else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { activeTab = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeTab) {
            0 -> {
                // List of Playlists
                if (playlists.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "No playlists found. Tap 'Import M3U' to add a playlist.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(playlists) { pl ->
                            GlassCard {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = pl.title,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Source: ${pl.sourceType} • ${pl.channelCount} Channels",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "${pl.liveCount} Live TV  |  ${pl.vodCount} Movies / VOD  |  ${pl.seriesCount} Series",
                                                color = ElectricOrange,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.deletePlaylist(pl.id) }
                                        ) {
                                            Text(
                                                text = "DEL",
                                                color = StatusCrimson,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        NovaPrimaryButton(
                                            text = "View Channels",
                                            onClick = { viewModel.switchScreen(Screen.CHANNELS) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        NovaSecondaryButton(
                                            text = "Direct Player",
                                            onClick = { viewModel.switchScreen(Screen.PLAYER) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Import Playlist Form
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        GlassCard {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "IMPORT M3U / M3U8 PLAYLIST",
                                    color = ElectricOrange,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Mode Selector: Remote URL vs Direct Text Paste
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    NovaSecondaryButton(
                                        text = "Remote URL (HTTP/HTTPS)",
                                        onClick = { importIsUrlMode = true },
                                        borderColor = if (importIsUrlMode) ElectricOrange else Color.Gray,
                                        modifier = Modifier.weight(1f)
                                    )
                                    NovaSecondaryButton(
                                        text = "Paste Raw Content",
                                        onClick = { importIsUrlMode = false },
                                        borderColor = if (!importIsUrlMode) ElectricOrange else Color.Gray,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Playlist Title",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                NovaInputField(
                                    value = importTitle,
                                    onValueChange = { importTitle = it },
                                    placeholder = "e.g. My Premium IPTV"
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                if (importIsUrlMode) {
                                    Text(
                                        text = "Playlist URL (M3U or M3U8)",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    NovaInputField(
                                        value = importUrl,
                                        onValueChange = { importUrl = it },
                                        placeholder = "https://example.com/playlist.m3u8"
                                    )
                                } else {
                                    Text(
                                        text = "Raw M3U Content (#EXTM3U ...)",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    NovaInputField(
                                        value = importContent,
                                        onValueChange = { importContent = it },
                                        placeholder = "#EXTM3U\n#EXTINF:-1,Sample Channel\nhttps://...",
                                        minLines = 6
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (isImporting) {
                                    CircularProgressIndicator(
                                        color = ElectricOrange,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                } else {
                                    NovaPrimaryButton(
                                        text = "Parse & Import Channels",
                                        onClick = {
                                            if (importIsUrlMode) {
                                                viewModel.importPlaylist(importTitle, importUrl, true)
                                            } else {
                                                viewModel.importPlaylist(importTitle, importContent, false)
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = (importIsUrlMode && importUrl.isNotBlank()) || (!importIsUrlMode && importContent.isNotBlank())
                                    )
                                }

                                if (importMessage != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    val isErr = importMessage!!.startsWith("Import Error")
                                    Text(
                                        text = importMessage!!,
                                        color = if (isErr) StatusCrimson else StatusEmerald,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Combo Editor Tool (iptvhit-inspired)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        GlassCard {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "COMBOLIST FIXER & FORMATTER",
                                    color = NeonViolet,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Automatically repair delimiter errors, remove duplicates, and normalize 'user:pass' combinations.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                NovaInputField(
                                    value = rawComboText,
                                    onValueChange = { rawComboText = it },
                                    placeholder = "Paste combos here:\nuser1;pass1\nuser2\tpass2\nuser1:pass1 (duplicate)",
                                    minLines = 6
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                NovaPrimaryButton(
                                    text = "Fix & Deduplicate Combos",
                                    onClick = { viewModel.fixCombolist(rawComboText) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = rawComboText.isNotBlank()
                                )

                                if (fixedCombos.isNotEmpty() || fixedErrorsCount > 0) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "Processed: ${fixedCombos.size} valid entries  •  Fixed/Removed: $fixedErrorsCount errors",
                                        color = StatusEmerald,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = fixedCombos.take(20).joinToString("\n") + if (fixedCombos.size > 20) "\n... (+${fixedCombos.size - 20} more)" else "",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NovaInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .padding(12.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp
            ),
            cursorBrush = SolidColor(ElectricOrange),
            minLines = minLines,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

package com.novastream.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastream.ai.ui.components.GlassCard
import com.novastream.ai.ui.components.NovaPrimaryButton
import com.novastream.ai.ui.components.NovaSecondaryButton
import com.novastream.ai.ui.theme.*
import com.novastream.ai.ui.viewmodel.NovaViewModel

@Composable
fun DiagnosticsScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val diagnosticsResult by viewModel.diagnosticsResult.collectAsState()
    val isTesting by viewModel.isTestingDiagnostics.collectAsState()
    val channels by viewModel.channels.collectAsState()

    var testUrlInput by remember { mutableStateOf("") }
    var selectedSpeedMode by remember { mutableStateOf("Turbo (Fast Ping)") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "REAL-TIME STREAM DIAGNOSTICS & AUDIT",
                        color = ElectricOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Inspired by high-speed IPTV checkers: Test stream availability, HTTP response codes, server latency, and proxy status.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Target Stream URL",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    NovaInputField(
                        value = testUrlInput,
                        onValueChange = { testUrlInput = it },
                        placeholder = "https://example.com/live/channel.m3u8"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Select from Channels
                    if (channels.isNotEmpty()) {
                        Text(
                            text = "Or pick from channel list:",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            channels.take(3).forEach { ch ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { testUrlInput = ch.streamUrl }
                                        .padding(vertical = 6.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ch.name,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Speed Modes Selector
                    Text(
                        text = "Diagnostic Speed Mode:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = listOf("Normal", "Fast", "Turbo (Fast Ping)")
                        modes.forEach { mode ->
                            val isSelected = selectedSpeedMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonViolet else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { selectedSpeedMode = mode }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isTesting) {
                        CircularProgressIndicator(
                            color = ElectricOrange,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        NovaPrimaryButton(
                            text = "EXECUTE DIAGNOSTIC PING",
                            onClick = {
                                if (testUrlInput.isNotBlank()) {
                                    viewModel.testStream(testUrlInput)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = testUrlInput.isNotBlank()
                        )
                    }
                }
            }
        }

        // Diagnostics Result Section
        if (diagnosticsResult != null) {
            val res = diagnosticsResult!!
            item {
                GlassCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DIAGNOSTIC REPORT",
                                color = if (res.isReachable) StatusEmerald else StatusCrimson,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (res.isReachable) StatusEmerald.copy(alpha = 0.2f) else StatusCrimson.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (res.isReachable) "ONLINE (${res.httpStatus})" else "FAILED (${res.httpStatus})",
                                    color = if (res.isReachable) StatusEmerald else StatusCrimson,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        DiagnosticRow("Target URL", res.url)
                        DiagnosticRow("Latency", "${res.responseTimeMs} ms")
                        DiagnosticRow("HTTP Status", "${res.httpStatus}")
                        DiagnosticRow("Content-Type", res.contentType ?: "Unknown")
                        DiagnosticRow("Server Header", res.serverHeader ?: "Standard CDN")

                        if (res.errorDetails != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Error Details: ${res.errorDetails}",
                                color = StatusCrimson,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        if (res.isReachable) {
                            NovaPrimaryButton(
                                text = "PLAY TESTED STREAM",
                                onClick = { viewModel.playDirectUrl("Tested Stream", res.url) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Network Information Card (iptvhit-inspired client info)
        item {
            GlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CLIENT NETWORK PROFILE",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DiagnosticRow("Local Engine", "Nova Stream AI Core 1.0")
                    DiagnosticRow("Protocol", "HTTPS / HTTP Direct Transport")
                    DiagnosticRow("Android TV Leanback", "Supported")
                    DiagnosticRow("Hardware Codec", "Auto H.264 / HEVC Decoder")
                }
            }
        }
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

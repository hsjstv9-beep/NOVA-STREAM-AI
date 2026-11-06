package com.novastream.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastream.ai.data.model.Screen
import com.novastream.ai.ui.components.GlassCard
import com.novastream.ai.ui.components.NovaPrimaryButton
import com.novastream.ai.ui.components.NovaSecondaryButton
import com.novastream.ai.ui.theme.*
import com.novastream.ai.ui.viewmodel.NovaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val playlists by viewModel.playlists.collectAsState()
    val channels by viewModel.channels.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()

    val totalChannels = channels.size
    val liveCount = channels.count { it.contentType.name == "LIVE" }
    val vodCount = channels.count { it.contentType.name == "VOD" }
    val totalPlaylists = playlists.size

    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Hero Banner Card
        item {
            GlassCard(
                borderBrush = Brush.linearGradient(listOf(ElectricOrange, NeonViolet))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NOVA INTELLIGENCE DASHBOARD",
                            color = ElectricOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(StatusEmerald.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SYSTEM HEALTHY",
                                color = StatusEmerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Next-Generation Stream Orchestration",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Monitor, inspect, validate, and play high-definition authorized streams with zero buffer delay.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NovaPrimaryButton(
                            text = "Import M3U / M3U8",
                            onClick = { viewModel.switchScreen(Screen.PLAYLISTS) },
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

        // 4 Key Statistics Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Playlists",
                    value = totalPlaylists.toString(),
                    subtitle = "Active Sources",
                    accentColor = ElectricOrange,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Channels",
                    value = totalChannels.toString(),
                    subtitle = "$liveCount Live / $vodCount VOD",
                    accentColor = NeonViolet,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Favorites",
                    value = favorites.size.toString(),
                    subtitle = "Pinned Channels",
                    accentColor = StatusEmerald,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Engine",
                    value = "Android TV",
                    subtitle = "Leanback Ready",
                    accentColor = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Actions Section
        item {
            Text(
                text = "QUICK ACTIONS",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.switchScreen(Screen.CHANNELS) }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Browse Channels",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Search & Filter",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.switchScreen(Screen.DIAGNOSTICS) }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Stream Diagnostics",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Speed & Ping Test",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Recently Added Playlists Section
        item {
            Text(
                text = "RECENT PLAYLISTS",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        if (playlists.isEmpty()) {
            item {
                GlassCard {
                    Text(
                        text = "No playlists added yet. Tap 'Import M3U / M3U8' to add your first playlist.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(playlists.take(3)) { pl ->
                GlassCard(
                    onClick = { viewModel.switchScreen(Screen.CHANNELS) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = pl.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${pl.channelCount} Channels (${pl.liveCount} Live, ${pl.vodCount} VOD)",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonViolet.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = pl.sourceType,
                                color = NeonViolet,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Activity Log Timeline
        item {
            Text(
                text = "SYSTEM ACTIVITY LOG",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        if (activityLogs.isEmpty()) {
            item {
                GlassCard {
                    Text(
                        text = "No recent activity.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(activityLogs.take(5)) { log ->
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = log.description,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
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
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column {
            Text(
                text = title.uppercase(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = accentColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastream.ai.data.model.ContentType
import com.novastream.ai.ui.components.GlassCard
import com.novastream.ai.ui.components.NovaSecondaryButton
import com.novastream.ai.ui.theme.*
import com.novastream.ai.ui.viewmodel.NovaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyticsScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val channels by viewModel.channels.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val history by viewModel.history.collectAsState()

    val total = channels.size.coerceAtLeast(1)
    val liveCount = channels.count { it.contentType == ContentType.LIVE }
    val vodCount = channels.count { it.contentType == ContentType.VOD }
    val seriesCount = channels.count { it.contentType == ContentType.SERIES }

    val liveRatio = liveCount.toFloat() / total
    val vodRatio = vodCount.toFloat() / total
    val seriesRatio = seriesCount.toFloat() / total

    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

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
                        text = "STREAM ANALYTICS & DISTRIBUTION",
                        color = ElectricOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Multi-Segment Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                    ) {
                        if (liveRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(liveRatio)
                                    .fillMaxHeight()
                                    .background(CyberCyan)
                            )
                        }
                        if (vodRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(vodRatio)
                                    .fillMaxHeight()
                                    .background(ElectricOrange)
                            )
                        }
                        if (seriesRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(seriesRatio)
                                    .fillMaxHeight()
                                    .background(NeonViolet)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LegendItem("Live Channels", "$liveCount (${(liveRatio * 100).toInt()}%)", CyberCyan)
                        LegendItem("VOD Movies", "$vodCount (${(vodRatio * 100).toInt()}%)", ElectricOrange)
                        LegendItem("Series", "$seriesCount (${(seriesRatio * 100).toInt()}%)", NeonViolet)
                    }
                }
            }
        }

        // Top Categories Breakdown
        item {
            GlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "TOP CATEGORIES",
                        color = NeonViolet,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (categories.isEmpty()) {
                        Text(
                            text = "No categories available.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    } else {
                        categories.take(6).forEach { cat ->
                            val catRatio = cat.channelCount.toFloat() / total
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = cat.name,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${cat.channelCount} channels",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(catRatio.coerceIn(0.05f, 1f))
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(NeonViolet)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Viewing History Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT VIEWING HISTORY",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (history.isNotEmpty()) {
                    Text(
                        text = "Clear History",
                        color = StatusCrimson,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.clearViewingHistory() }
                    )
                }
            }
        }

        if (history.isEmpty()) {
            item {
                GlassCard {
                    Text(
                        text = "No viewing history recorded yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(history) { item ->
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.channelName,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.category} • ${dateFormat.format(Date(item.lastWatchedAt))}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        NovaSecondaryButton(
                            text = "REPLAY",
                            onClick = {
                                viewModel.playDirectUrl(item.channelName, item.streamUrl)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, count: String, color: Color) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = count,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

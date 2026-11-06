package com.novastream.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.novastream.ai.data.model.ContentType
import com.novastream.ai.ui.components.ContentTypeBadge
import com.novastream.ai.ui.components.GlassCard
import com.novastream.ai.ui.components.NovaPrimaryButton
import com.novastream.ai.ui.components.NovaSecondaryButton
import com.novastream.ai.ui.components.StatusBadge
import com.novastream.ai.ui.theme.ElectricOrange
import com.novastream.ai.ui.theme.StatusAmber
import com.novastream.ai.ui.viewmodel.NovaViewModel

@Composable
fun ChannelListScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val channels by viewModel.channels.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()

    var filterType by remember { mutableStateOf("ALL") } // ALL, LIVE, VOD, SERIES, FAVORITES

    val filteredChannels = remember(channels, searchQuery, selectedCategory, filterType, favorites) {
        channels.filter { ch ->
            val matchesSearch = searchQuery.isBlank() ||
                    ch.name.contains(searchQuery, ignoreCase = true) ||
                    ch.category.contains(searchQuery, ignoreCase = true)

            val matchesType = when (filterType) {
                "ALL" -> true
                "LIVE" -> ch.contentType == ContentType.LIVE
                "VOD" -> ch.contentType == ContentType.VOD
                "SERIES" -> ch.contentType == ContentType.SERIES
                "FAVORITES" -> ch.isFavorite
                else -> true
            }

            val matchesCategory = selectedCategory == "ALL" || ch.category.equals(selectedCategory, ignoreCase = true)

            matchesSearch && matchesType && matchesCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search Input Bar
        NovaInputField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = "Search stream, channel name, category...",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Type Filter Pills
        val typePills = listOf("ALL", "LIVE", "VOD", "SERIES", "FAVORITES")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            typePills.forEach { pill ->
                val isSelected = filterType == pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ElectricOrange else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { filterType = pill }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = pill,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Category Filter Chips
        if (categories.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val catList = listOf("ALL") + categories.map { it.name }.distinct()
                catList.take(15).forEach { catName ->
                    val isCatSelected = selectedCategory == catName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .border(
                                width = 1.dp,
                                color = if (isCatSelected) ElectricOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .background(if (isCatSelected) ElectricOrange.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { viewModel.setCategoryFilter(catName) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = catName,
                            color = if (isCatSelected) ElectricOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Channel Count Header
        Text(
            text = "SHOWING ${filteredChannels.size} OF ${channels.size} CHANNELS",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Channels List
        if (filteredChannels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No channels match your query.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredChannels, key = { it.id }) { ch ->
                    GlassCard(
                        onClick = { viewModel.playChannel(ch) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ContentTypeBadge(type = ch.contentType)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = ch.name,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Category: ${ch.category}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(isWorking = ch.isWorking, latencyMs = ch.latencyMs)
                                Spacer(modifier = Modifier.width(8.dp))

                                // Favorite Star Toggle
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.toggleFavorite(ch) }
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = if (ch.isFavorite) "★" else "☆",
                                        color = if (ch.isFavorite) StatusAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))
                                NovaPrimaryButton(
                                    text = "PLAY",
                                    onClick = { viewModel.playChannel(ch) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

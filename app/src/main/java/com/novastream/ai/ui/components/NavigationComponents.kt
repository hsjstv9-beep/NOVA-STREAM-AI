package com.novastream.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastream.ai.data.model.Screen
import com.novastream.ai.ui.theme.ElectricOrange
import com.novastream.ai.ui.theme.NeonViolet
import com.novastream.ai.ui.theme.TextWhitePrimary

@Composable
fun NovaNavigationTabBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val screens = listOf(
            Screen.DASHBOARD,
            Screen.PLAYLISTS,
            Screen.CHANNELS,
            Screen.PLAYER,
            Screen.DIAGNOSTICS,
            Screen.ANALYTICS,
            Screen.PROFILES,
            Screen.SETTINGS
        )

        screens.forEach { screen ->
            val isSelected = currentScreen == screen
            val interactionSource = remember { MutableInteractionSource() }
            val isFocused by interactionSource.collectIsFocusedAsState()

            val shape = RoundedCornerShape(12.dp)
            val bgBrush = when {
                isSelected -> Brush.horizontalGradient(listOf(ElectricOrange, NeonViolet))
                isFocused -> Brush.horizontalGradient(listOf(ElectricOrange.copy(alpha = 0.4f), NeonViolet.copy(alpha = 0.4f)))
                else -> Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
            }

            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(bgBrush)
                    .then(
                        if (isFocused) {
                            Modifier.border(2.dp, Color.White, shape)
                        } else if (!isSelected) {
                            Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), shape)
                        } else {
                            Modifier
                        }
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onScreenSelected(screen) }
                    )
                    .focusable(interactionSource = interactionSource)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = screen.title,
                    color = if (isSelected || isFocused) TextWhitePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

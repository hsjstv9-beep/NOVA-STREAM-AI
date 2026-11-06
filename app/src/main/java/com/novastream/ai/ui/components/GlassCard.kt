package com.novastream.ai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.novastream.ai.ui.theme.ElectricOrange
import com.novastream.ai.ui.theme.NeonViolet

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    borderBrush: Brush? = null,
    backgroundColor: Color? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape = RoundedCornerShape(cornerRadius)
    val defaultBorder = borderBrush ?: Brush.linearGradient(
        colors = if (isFocused) {
            listOf(ElectricOrange, NeonViolet)
        } else {
            listOf(
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        }
    )

    val surfaceColor = backgroundColor ?: MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)

    Box(
        modifier = modifier
            .clip(shape)
            .border(
                border = BorderStroke(if (isFocused) 2.dp else 1.dp, defaultBorder),
                shape = shape
            )
            .background(surfaceColor)
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                        .focusable(interactionSource = interactionSource)
                } else {
                    Modifier
                }
            )
            .padding(16.dp)
    ) {
        content()
    }
}

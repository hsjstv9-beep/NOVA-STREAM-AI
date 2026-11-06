package com.novastream.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.novastream.ai.data.model.UserProfile
import com.novastream.ai.ui.theme.ElectricOrange
import com.novastream.ai.ui.theme.NeonViolet
import com.novastream.ai.ui.theme.TextWhitePrimary

@Composable
fun HeaderBar(
    title: String,
    currentUser: UserProfile?,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Nova Icon Dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(ElectricOrange, NeonViolet))
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NOVA STREAM AI",
                    color = ElectricOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Active Profile Pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) ElectricOrange else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(20.dp)
                )
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onProfileClick
                )
                .focusable(interactionSource = interactionSource)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NeonViolet, ElectricOrange))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentUser?.name?.take(1)?.uppercase() ?: "A",
                    color = TextWhitePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = currentUser?.name ?: "Admin",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

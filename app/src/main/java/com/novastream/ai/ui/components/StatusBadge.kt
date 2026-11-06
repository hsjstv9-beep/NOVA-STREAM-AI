package com.novastream.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastream.ai.data.model.ContentType
import com.novastream.ai.ui.theme.*

@Composable
fun StatusBadge(
    isWorking: Boolean,
    latencyMs: Long = 0,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isWorking) StatusEmerald.copy(alpha = 0.2f) else StatusCrimson.copy(alpha = 0.2f)
    val dotColor = if (isWorking) StatusEmerald else StatusCrimson
    val text = if (isWorking) {
        if (latencyMs > 0) "${latencyMs}ms" else "ONLINE"
    } else {
        "OFFLINE"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = dotColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ContentTypeBadge(
    type: ContentType,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (type) {
        ContentType.LIVE -> Triple(CyberCyan.copy(alpha = 0.2f), CyberCyan, "LIVE")
        ContentType.VOD -> Triple(ElectricOrange.copy(alpha = 0.2f), ElectricOrange, "VOD")
        ContentType.SERIES -> Triple(NeonViolet.copy(alpha = 0.2f), NeonViolet, "SERIES")
        ContentType.RADIO -> Triple(StatusAmber.copy(alpha = 0.2f), StatusAmber, "RADIO")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

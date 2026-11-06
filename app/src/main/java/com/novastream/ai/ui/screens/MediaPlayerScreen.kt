package com.novastream.ai.ui.screens

import android.net.Uri
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.novastream.ai.data.model.Channel
import com.novastream.ai.ui.components.ContentTypeBadge
import com.novastream.ai.ui.components.GlassCard
import com.novastream.ai.ui.components.NovaPrimaryButton
import com.novastream.ai.ui.components.NovaSecondaryButton
import com.novastream.ai.ui.theme.*
import com.novastream.ai.ui.viewmodel.NovaViewModel
import kotlinx.coroutines.delay

@Composable
fun MediaPlayerScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val playingChannel by viewModel.playingChannel.collectAsState()
    val channels by viewModel.channels.collectAsState()

    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPosMs by remember { mutableStateOf(0) }
    var durationMs by remember { mutableStateOf(0) }
    var isFullscreen by remember { mutableStateOf(false) }
    var aspectRatioMode by remember { mutableStateOf("16:9") } // 16:9, FIT, FILL
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var directUrlInput by remember { mutableStateOf("") }
    var showDirectInput by remember { mutableStateOf(false) }

    // Position updater loop
    LaunchedEffect(playingChannel, isPlaying) {
        while (true) {
            videoViewRef?.let { vv ->
                try {
                    if (vv.isPlaying) {
                        currentPosMs = vv.currentPosition
                        durationMs = vv.duration
                        isPlaying = true
                        isBuffering = false
                    }
                } catch (e: Exception) {
                    // Ignore errors when video isn't ready yet
                }
            }
            delay(500)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = if (isFullscreen) 0.dp else 16.dp)
    ) {
        // Direct URL Stream Input Bar (Toggleable)
        if (!isFullscreen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HARDWARE-ACCELERATED STREAM ENGINE",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (showDirectInput) "Close Direct URL" else "+ Direct Stream URL",
                    color = NeonViolet,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showDirectInput = !showDirectInput }
                )
            }

            if (showDirectInput) {
                GlassCard(modifier = Modifier.padding(bottom = 12.dp)) {
                    Column {
                        Text(
                            text = "Stream M3U8 / MP4 URL:",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        NovaInputField(
                            value = directUrlInput,
                            onValueChange = { directUrlInput = it },
                            placeholder = "https://server.domain/live/stream.m3u8"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        NovaPrimaryButton(
                            text = "PLAY DIRECT STREAM",
                            onClick = {
                                if (directUrlInput.isNotBlank()) {
                                    viewModel.playDirectUrl("Direct Stream", directUrlInput)
                                    showDirectInput = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = directUrlInput.isNotBlank()
                        )
                    }
                }
            }
        }

        // Main Video Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isFullscreen) {
                        Modifier.fillMaxHeight()
                    } else {
                        when (aspectRatioMode) {
                            "16:9" -> Modifier.aspectRatio(16f / 9f)
                            "4:3" -> Modifier.aspectRatio(4f / 3f)
                            else -> Modifier.height(240.dp)
                        }
                    }
                )
                .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 16.dp))
                .border(
                    width = if (isFullscreen) 0.dp else 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(if (isFullscreen) 0.dp else 16.dp)
                )
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // Android VideoView implementation
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    VideoView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                        setOnPreparedListener { mp ->
                            mp.isLooping = false
                            isBuffering = false
                            errorMessage = null
                            start()
                            isPlaying = true
                            durationMs = duration
                        }
                        setOnErrorListener { _, what, extra ->
                            isBuffering = false
                            isPlaying = false
                            errorMessage = "Playback Error (Code $what, $extra). Check stream link or connection."
                            true
                        }
                        videoViewRef = this
                    }
                },
                update = { view ->
                    playingChannel?.let { ch ->
                        try {
                            val uri = Uri.parse(ch.streamUrl)
                            view.setVideoURI(uri)
                            isBuffering = true
                            errorMessage = null
                            view.start()
                        } catch (e: Exception) {
                            errorMessage = "Error initializing stream: ${e.message}"
                        }
                    }
                }
            )

            // Buffering Spinner
            if (isBuffering && errorMessage == null) {
                CircularProgressIndicator(
                    color = ElectricOrange,
                    modifier = Modifier.size(48.dp)
                )
            }

            // Error Overlay
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "STREAM UNAVAILABLE",
                            color = StatusCrimson,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        NovaSecondaryButton(
                            text = "RETRY STREAM",
                            onClick = {
                                playingChannel?.let { ch ->
                                    videoViewRef?.setVideoURI(Uri.parse(ch.streamUrl))
                                    videoViewRef?.start()
                                    isBuffering = true
                                    errorMessage = null
                                }
                            }
                        )
                    }
                }
            }

            // Top Status Bar Inside Video
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                        )
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    playingChannel?.let { ContentTypeBadge(type = it.contentType) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = playingChannel?.name ?: "No Channel Selected",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                // Fullscreen Toggle
                PlayerControlIcon(
                    text = if (isFullscreen) "EXIT" else "FULL",
                    onClick = { isFullscreen = !isFullscreen }
                )
            }

            // Bottom Player Control Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Jump -10s
                    PlayerControlIcon(
                        text = "-10s",
                        onClick = {
                            videoViewRef?.let { vv ->
                                val target = (vv.currentPosition - 10000).coerceAtLeast(0)
                                vv.seekTo(target)
                            }
                        }
                    )

                    // Play / Pause Main Button
                    PlayerPlayPauseButton(
                        isPlaying = isPlaying,
                        onClick = {
                            videoViewRef?.let { vv ->
                                if (vv.isPlaying) {
                                    vv.pause()
                                    isPlaying = false
                                } else {
                                    vv.start()
                                    isPlaying = true
                                }
                            }
                        }
                    )

                    // Jump +10s
                    PlayerControlIcon(
                        text = "+10s",
                        onClick = {
                            videoViewRef?.let { vv ->
                                val target = vv.currentPosition + 10000
                                vv.seekTo(target)
                            }
                        }
                    )
                }

                // Aspect Ratio Switcher
                PlayerControlIcon(
                    text = aspectRatioMode,
                    onClick = {
                        aspectRatioMode = when (aspectRatioMode) {
                            "16:9" -> "4:3"
                            "4:3" -> "FILL"
                            else -> "16:9"
                        }
                    }
                )
            }
        }

        if (!isFullscreen) {
            Spacer(modifier = Modifier.height(14.dp))

            // Channel Quick-Switcher Carousel
            Text(
                text = "QUICK CHANNEL SWITCHER",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(channels) { ch ->
                    val isCurrent = ch.id == playingChannel?.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) ElectricOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(if (isCurrent) ElectricOrange.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.playChannel(ch) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = ch.name,
                                color = if (isCurrent) ElectricOrange else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = ch.category,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stream Technical Specifications Card
            GlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "STREAM TECHNICAL SPECIFICATIONS",
                        color = NeonViolet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "URL: ${playingChannel?.streamUrl ?: "None"}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Codec / Container: HLS (.m3u8) / MPEG-TS / MP4",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Hardware Accel: Enabled",
                            color = StatusEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerPlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(ElectricOrange, NeonViolet)))
            .then(
                if (isFocused) {
                    Modifier.border(2.dp, Color.White, CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isPlaying) "❚❚" else "▶",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerControlIcon(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = if (isFocused) 0.35f else 0.15f))
            .then(
                if (isFocused) {
                    Modifier.border(2.dp, Color.White, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

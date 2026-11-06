package com.novastream.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastream.ai.data.model.UserProfile
import com.novastream.ai.ui.components.GlassCard
import com.novastream.ai.ui.components.NovaPrimaryButton
import com.novastream.ai.ui.components.NovaSecondaryButton
import com.novastream.ai.ui.theme.*
import com.novastream.ai.ui.viewmodel.NovaViewModel

@Composable
fun UserProfileScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val profiles by viewModel.profiles.collectAsState()

    var showAddModal by remember { mutableStateOf(false) }
    var newProfileName by remember { mutableStateOf("") }
    var newProfilePin by remember { mutableStateOf("") }
    var newProfileIsKids by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Active Profile Card
        item {
            GlassCard(
                borderBrush = Brush.linearGradient(listOf(ElectricOrange, NeonViolet))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonViolet, ElectricOrange))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.name?.take(1)?.uppercase() ?: "A",
                            color = TextWhitePrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.name ?: "Admin",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ElectricOrange.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = ElectricOrange,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (currentUser?.isKidsMode == true) "Mode: Family / Kids Safe" else "Mode: Unrestricted Admin",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (currentUser?.pinCode != null) "Security: PIN Protected" else "Security: Open Profile",
                            color = if (currentUser?.pinCode != null) StatusEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Section Title & Add Profile Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVAILABLE PROFILES (${profiles.size})",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (showAddModal) "Cancel" else "+ Add New Profile",
                    color = ElectricOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showAddModal = !showAddModal }
                )
            }
        }

        // Add New Profile Card Form
        if (showAddModal) {
            item {
                GlassCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "CREATE NEW USER PROFILE",
                            color = ElectricOrange,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Profile Name",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        NovaInputField(
                            value = newProfileName,
                            onValueChange = { newProfileName = it },
                            placeholder = "e.g. Living Room TV or Family"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Optional 4-Digit Security PIN (leave blank for open access)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        NovaInputField(
                            value = newProfilePin,
                            onValueChange = { if (it.length <= 4) newProfilePin = it },
                            placeholder = "e.g. 1234"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { newProfileIsKids = !newProfileIsKids }
                        ) {
                            Checkbox(
                                checked = newProfileIsKids,
                                onCheckedChange = { newProfileIsKids = it },
                                colors = CheckboxDefaults.colors(checkedColor = ElectricOrange)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Enable Kids Mode (Parental Filter)",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        NovaPrimaryButton(
                            text = "Save Profile",
                            onClick = {
                                if (newProfileName.isNotBlank()) {
                                    viewModel.createProfile(
                                        newProfileName,
                                        newProfilePin.ifBlank { null },
                                        newProfileIsKids
                                    )
                                    newProfileName = ""
                                    newProfilePin = ""
                                    newProfileIsKids = false
                                    showAddModal = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = newProfileName.isNotBlank()
                        )
                    }
                }
            }
        }

        // Profiles List
        items(profiles) { profile ->
            val isActive = profile.id == currentUser?.id
            GlassCard(
                onClick = { viewModel.switchProfile(profile) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isActive) ElectricOrange else MaterialTheme.colorScheme.outline),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.name.take(1).uppercase(),
                                color = TextWhitePrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = profile.name,
                                color = if (isActive) ElectricOrange else MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (profile.isKidsMode) "Kids Mode" else "Standard Profile",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isActive) {
                            NovaSecondaryButton(
                                text = "Switch",
                                onClick = { viewModel.switchProfile(profile) }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { viewModel.deleteProfile(profile.id) }
                            ) {
                                Text(
                                    text = "✕",
                                    color = StatusCrimson,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = "CURRENT",
                                color = StatusEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

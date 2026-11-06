package com.novastream.ai

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.novastream.ai.data.model.Screen
import com.novastream.ai.ui.components.HeaderBar
import com.novastream.ai.ui.components.NovaNavigationTabBar
import com.novastream.ai.ui.screens.*
import com.novastream.ai.ui.theme.NovaStreamAITheme
import com.novastream.ai.ui.viewmodel.NovaViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: NovaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = NovaStreamApp.instance.repository
        viewModel = NovaViewModel(repository)

        setContent {
            val settings by viewModel.settings.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()

            NovaStreamAITheme(themeMode = settings.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            HeaderBar(
                                title = currentScreen.title,
                                currentUser = currentUser,
                                onProfileClick = { viewModel.switchScreen(Screen.PROFILES) }
                            )
                        },
                        bottomBar = {
                            NovaNavigationTabBar(
                                currentScreen = currentScreen,
                                onScreenSelected = { viewModel.switchScreen(it) }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            when (currentScreen) {
                                Screen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                                Screen.PLAYLISTS -> PlaylistManagerScreen(viewModel = viewModel)
                                Screen.CHANNELS -> ChannelListScreen(viewModel = viewModel)
                                Screen.PLAYER -> MediaPlayerScreen(viewModel = viewModel)
                                Screen.DIAGNOSTICS -> DiagnosticsScreen(viewModel = viewModel)
                                Screen.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                                Screen.PROFILES -> UserProfileScreen(viewModel = viewModel)
                                Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (viewModel.currentScreen.value != Screen.DASHBOARD) {
                    viewModel.switchScreen(Screen.DASHBOARD)
                    true
                } else {
                    super.onKeyDown(keyCode, event)
                }
            }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                // If on player screen, toggle
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}

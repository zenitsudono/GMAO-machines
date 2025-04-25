package com.app.gmao_machines.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.gmao_machines.models.Screen
import com.app.gmao_machines.ui.components.FloatingBottomBar
import com.app.gmao_machines.ui.viewModel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onSignOut: () -> Unit = {}
) {
    val currentScreen = viewModel.currentScreen.collectAsState().value
    val navigationItems = listOf(Screen.Home, Screen.History, Screen.Profile)
    
    // Track if we're in a subscreen that should hide the bottom bar
    var showBottomBar by remember { mutableStateOf(true) }
    
    // Callback to be passed to Profile screen to hide/show bottom bar
    val onSubScreenChange: (Boolean) -> Unit = { isInSubScreen ->
        showBottomBar = !isInSubScreen
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            when (currentScreen) {
                Screen.Home -> Text("Home Screen")
                Screen.History -> Text("History Screen")
                Screen.Profile -> ProfileScreen(
                    onSignOut = onSignOut,
                    onSubScreenChange = onSubScreenChange
                )
            }
        }
        
        // FloatingBottomBar with high zIndex to appear above all content
        // Only show if not in a subscreen
        if (showBottomBar) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .zIndex(10f),
                contentAlignment = Alignment.Center
            ) {
                FloatingBottomBar(
                    items = navigationItems,
                    currentScreen = currentScreen,
                    onScreenSelected = { viewModel.navigateTo(it) }
                )
            }
        }
    }
}

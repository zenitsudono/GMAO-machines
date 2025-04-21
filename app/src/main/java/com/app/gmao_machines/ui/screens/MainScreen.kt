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

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                FloatingBottomBar(
                    items = navigationItems,
                    currentScreen = currentScreen,
                    onScreenSelected = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (currentScreen) {
                Screen.Home -> Text("Home Screen")
                Screen.History -> Text("History Screen")
                Screen.Profile -> ProfileScreen(onSignOut = onSignOut)
            }
        }
    }
}

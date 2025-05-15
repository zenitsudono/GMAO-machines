package com.app.gmao_machines.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.app.gmao_machines.data.Intervention
import com.app.gmao_machines.models.Screen
import com.app.gmao_machines.ui.components.Calendar
import com.app.gmao_machines.ui.components.FloatingBottomBar
import com.app.gmao_machines.ui.viewModel.HistoryViewModel
import com.app.gmao_machines.ui.viewModel.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
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

    var selectedIntervention by remember { mutableStateOf<Intervention?>(null) }
    val historyViewModel: HistoryViewModel = viewModel()
    val interventions by historyViewModel.interventions.collectAsState()

    if (selectedIntervention != null) {
        InterventionDetailScreen(
            intervention = selectedIntervention!!,
            onBackClick = { selectedIntervention = null }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.TopCenter
            ) {
                when (currentScreen) {
                    Screen.Home -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Calendar",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Calendar(interventions = interventions)
                        }
                    }
                    Screen.History -> HistoryScreen(
                        viewModel = historyViewModel,
                        onInterventionClick = { intervention ->
                            selectedIntervention = intervention
                        }
                    )
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
}

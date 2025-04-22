package com.app.gmao_machines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import com.app.gmao_machines.navigation.AppNavigation
import com.app.gmao_machines.provider.ThemeViewModelProvider
import com.app.gmao_machines.ui.theme.*
import com.app.gmao_machines.ui.screens.EnhancedSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = ThemeViewModelProvider.getThemeViewModel(applicationContext)
            val isDarkTheme by themeViewModel.isDarkTheme

            GMAOMachinesTheme(darkTheme = isDarkTheme) {
                var showSplash by remember { mutableStateOf(true) }
                
                AnimatedVisibility(
                    visible = showSplash,
                    exit = fadeOut(animationSpec = tween(800))
                ) {
                    EnhancedSplashScreen {
                        showSplash = false
                    }
                }
                
                AnimatedVisibility(
                    visible = !showSplash,
                    enter = fadeIn(animationSpec = tween(800))
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
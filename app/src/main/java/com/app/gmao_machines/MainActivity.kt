package com.app.gmao_machines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import com.app.gmao_machines.navigation.AppNavigation
import com.app.gmao_machines.ui.theme.*
import com.app.gmao_machines.ui.screens.EnhancedSplashScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            GMAOMachinesTheme {
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






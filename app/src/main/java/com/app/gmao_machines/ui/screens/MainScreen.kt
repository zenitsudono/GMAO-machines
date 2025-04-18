package com.app.gmao_machines.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.gmao_machines.models.Screen
import com.app.gmao_machines.ui.components.FloatingBottomBar
import com.app.gmao_machines.ui.theme.*
import com.app.gmao_machines.ui.viewModel.MainViewModel
import kotlin.math.sin

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val currentScreen = viewModel.currentScreen.collectAsState().value
    val navigationItems = listOf(Screen.Home, Screen.History, Screen.Profile)

    // Create wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "main_screen_animations")
    
    // Wave animation
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
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
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Surface,
                            Background,
                            Background.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // Enhanced background with gradient, circles, diagonal lines and waves
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = size.width
                val height = size.height
                
                // Draw subtle circles in the background
                val circleColor = Primary.copy(alpha = 0.03f)
                for (i in 1..5) {
                    val radius = (width / 10) * i
                    drawCircle(
                        color = circleColor,
                        radius = radius.toFloat(),
                        center = Offset(width / 2, height / 2)
                    )
                }
                
                // Draw diagonal lines
                val lineCount = 8
                val spacingX = width / lineCount
                val strokeWidth = 1.dp.toPx()
                
                for (i in 0..lineCount) {
                    val x = i * spacingX
                    drawLine(
                        color = Primary.copy(alpha = 0.02f),
                        start = Offset(x, 0f),
                        end = Offset(0f, height - x),
                        strokeWidth = strokeWidth
                    )
                    
                    drawLine(
                        color = Primary.copy(alpha = 0.02f),
                        start = Offset(width - x, 0f),
                        end = Offset(width, height - x),
                        strokeWidth = strokeWidth
                    )
                }
                
                // Draw wave patterns
                val waveColor = Accent.copy(alpha = 0.03f)
                val waveAmplitude = height * 0.02f
                val waveLength = width * 0.33f
                
                val path = Path()
                path.moveTo(0f, height * 0.25f)
                
                for (i in 0..width.toInt() step 5) {
                    val x = i.toFloat()
                    val offsetY = sin((x / waveLength) + wavePhase) * waveAmplitude
                    path.lineTo(x, height * 0.25f + offsetY)
                }
                
                val path2 = Path()
                path2.moveTo(0f, height * 0.75f)
                
                for (i in 0..width.toInt() step 5) {
                    val x = i.toFloat()
                    val offsetY = sin((x / waveLength) - wavePhase) * waveAmplitude
                    path2.lineTo(x, height * 0.75f + offsetY)
                }
                
                drawPath(path, color = waveColor, style = Stroke(width = 2.dp.toPx()))
                drawPath(path2, color = waveColor, style = Stroke(width = 2.dp.toPx()))
            }

            when (currentScreen) {
                Screen.Home -> Text("Home Screen")
                Screen.History -> Text("History Screen")
                Screen.Profile -> Text("Profile Screen")
            }
        }
    }
}
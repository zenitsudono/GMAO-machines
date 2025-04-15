package com.app.gmao_machines.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.gmao_machines.R
import com.app.gmao_machines.ui.theme.backgroundColor
import com.app.gmao_machines.ui.theme.splashIndicatorColor
import com.app.gmao_machines.ui.viewModel.SplashViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = viewModel(),
    onNavigateToMain: () -> Unit
) {
    // Observe navigation state from ViewModel
    val shouldNavigate by viewModel.navigateToMain.collectAsState()

    // Handle navigation
    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            onNavigateToMain()
            viewModel.onNavigationComplete()
        }
    }

    // Animation properties
    val infiniteTransition = rememberInfiniteTransition()

    // Logo scale animation
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Create animated dots for loading indicator
    val dotAlphas = List(3) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing, delayMillis = 200 * index),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Splash screen content
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.splash_icon), // Replace with your logo
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(160.dp)
                    .scale(logoScale)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Creative loading indicator with dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                dotAlphas.forEachIndexed { index, animatedAlpha ->
                    val alpha by animatedAlpha
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(12.dp)
                            .scale(0.8f + (alpha * 0.4f))
                            .alpha(alpha)
                            .background(color = splashIndicatorColor, shape = MaterialTheme.shapes.small)
                    )
                }
            }
        }
    }
}
package com.app.gmao_machines.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.gmao_machines.ui.screens.MainScreen
import com.app.gmao_machines.ui.screens.OnboardingScreen
import com.app.gmao_machines.ui.screens.SplashScreen
import com.app.gmao_machines.ui.viewModel.OnboardingViewModel

@Composable
fun AppNavigation(viewModel: OnboardingViewModel = viewModel()) {
    val isOnboardingComplete = viewModel.isComplete.value
    val navController = rememberNavController()

    val startDestination = if (isOnboardingComplete) "splash" else "onboarding"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("splash") {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(viewModel = viewModel) {
                navController.navigate("main") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }
        }

        composable("main") {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    MaterialTheme {
        MainScreen()
    }
}
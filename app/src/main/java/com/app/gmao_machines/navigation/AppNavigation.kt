package com.app.gmao_machines.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.app.gmao_machines.ui.screens.*
import com.app.gmao_machines.ui.viewModel.OnboardingViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(viewModel: OnboardingViewModel = viewModel()) {
    val navController = rememberNavController()
    
    // Determine start destination based on user status
    val startDestination = remember {
        when {
            !viewModel.isComplete.value -> "onboarding"
            FirebaseAuth.getInstance().currentUser == null -> "auth"
            else -> "main"
        }
    }
    
    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            OnboardingScreen(viewModel = viewModel) {
                // When onboarding is complete (Skip or Get Started pressed), go to auth
                navController.navigate("auth") {
                    // Clear the navigation stack to prevent going back
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        composable("auth") {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
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


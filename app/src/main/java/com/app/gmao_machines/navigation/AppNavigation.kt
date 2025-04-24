package com.app.gmao_machines.navigation

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
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
            !viewModel.isComplete.value -> {
                Log.d("AppNavigation", "Starting with onboarding screen")
                "onboarding"
            }
            FirebaseAuth.getInstance().currentUser == null -> {
                Log.d("AppNavigation", "User not authenticated, starting with auth screen")
                "auth"
            }
            else -> {
                Log.d("AppNavigation", "User already authenticated, starting with main screen")
                "main"
            }
        }
    }
    
    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            OnboardingScreen(viewModel = viewModel) {
                // When onboarding is complete (Skip or Get Started pressed), go to auth
                Log.d("AppNavigation", "Onboarding complete, navigating to auth")
                navController.navigate("auth") {
                    // Clear the navigation stack to prevent going back
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        composable("auth") {
            AuthScreen(
                onAuthSuccess = {
                    Log.d("AppNavigation", "Auth success, navigating to main")
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            Log.d("AppNavigation", "Displaying main screen")
            MyApp(
                onSignOut = {
                    Log.d("AppNavigation", "User signed out, navigating to auth")
                    navController.navigate("auth") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MyApp(onSignOut: () -> Unit = {}) {
    MaterialTheme {
        MainScreen(onSignOut = onSignOut)
    }
}


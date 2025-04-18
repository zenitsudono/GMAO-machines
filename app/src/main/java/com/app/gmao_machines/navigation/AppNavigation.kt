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
import kotlinx.coroutines.delay

@Composable
fun AppNavigation(viewModel: OnboardingViewModel = viewModel()) {
    val navController = rememberNavController()
    
    // Observe onboarding state for reference
    val isComplete by remember { viewModel.isComplete }
    
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                navigateAfterSplash(navController, viewModel)
            }
        }

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

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    // Add your splash animation/UI here
    LaunchedEffect(Unit) {
        delay(2000L) // 2 seconds splash delay
        onFinish()
    }
}

fun navigateAfterSplash(
    navController: NavHostController,
    onboardingViewModel: OnboardingViewModel
) {
    val isOnboardingComplete = onboardingViewModel.isComplete.value
    val isUserAuthenticated = FirebaseAuth.getInstance().currentUser != null

    when {
        !isOnboardingComplete -> {
            navController.navigate("onboarding") {
                popUpTo("splash") { inclusive = true }
            }
        }
        !isUserAuthenticated -> {
            navController.navigate("auth") {
                popUpTo("splash") { inclusive = true }
            }
        }
        else -> {
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}


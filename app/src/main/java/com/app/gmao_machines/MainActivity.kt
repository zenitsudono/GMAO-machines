package com.app.gmao_machines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.gmao_machines.navigation.AppNavigation
import com.app.gmao_machines.ui.screens.OnboardingScreen
import com.app.gmao_machines.ui.theme.GMAOMachinesTheme
import com.app.gmao_machines.ui.viewModel.OnboardingViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            splashScreenViewProvider.remove()
        } 

        super.onCreate(savedInstanceState)
        setContent {
            GMAOMachinesTheme {
                if (viewModel.isOnboardingCompleted()) {
                    AppNavigation()
                } else {
                    OnboardingScreen(
                        onComplete = { viewModel.completeOnboarding() }
                    )
                }
            }
        }
    }
}



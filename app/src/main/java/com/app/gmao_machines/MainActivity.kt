package com.app.gmao_machines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.gmao_machines.models.OnboardingPref
import com.app.gmao_machines.navigation.AppNavigation
import com.app.gmao_machines.ui.screens.OnboardingScreen
import com.app.gmao_machines.ui.theme.GMAOmachinesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GMAOmachinesTheme {
                if (!OnboardingPref.isOnboardingCompleted(this)) {
                    OnboardingScreen {
                        OnboardingPref.setOnboardingCompleted(this)
                        recreate()
                    }
                } else {
                    AppNavigation()
                }
            }
        }
    }
}



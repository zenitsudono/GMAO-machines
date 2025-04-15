package com.app.gmao_machines.models

import android.content.Context
import androidx.core.content.edit

object OnboardingPref {
    fun setOnboardingCompleted(context: Context) {
        val sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit() { putBoolean("onboarding_competed", true) }
    }
    fun isOnboardingCompleted(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("onboarding_completed", false)
    }

}
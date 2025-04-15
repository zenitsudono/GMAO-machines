package com.app.gmao_machines.models

import android.content.Context
import androidx.core.content.edit

class OnboardingPref(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }

    fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit() { putBoolean(KEY_ONBOARDING_COMPLETED, completed) }
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
}
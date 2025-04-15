package com.app.gmao_machines.ui.viewModel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.AndroidViewModel
import com.app.gmao_machines.models.OnboardingPref

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val onboardingPreferences = OnboardingPref(application)

    // Current page state
    private val _currentPage = mutableIntStateOf(0)
    val currentPage: State<Int> = _currentPage

    // Total number of onboarding pages
    val totalPages = 3

    // Function to navigate to the next page
    fun nextPage() {
        if (_currentPage.intValue < totalPages - 1) {
            _currentPage.intValue = _currentPage.intValue + 1
        }
    }
    fun previousPage() {
        if (_currentPage.intValue > 0) {
            _currentPage.intValue = _currentPage.intValue - 1
        }
    }

    // Jump to a specific page
    fun goToPage(page: Int) {
        if (page in 0 until totalPages) {
            _currentPage.intValue = page
        }
    }

    // Function to check if onboarding is completed
    fun isOnboardingCompleted(): Boolean {
        return onboardingPreferences.isOnboardingCompleted()
    }

    // Function to mark onboarding as completed
    fun completeOnboarding() {
        onboardingPreferences.setOnboardingCompleted(true)
    }

    // Function to check if this is the last page
    fun isLastPage(): Boolean {
        return _currentPage.intValue == totalPages - 1
    }

    // Function to check if this is the first page
    fun isFirstPage(): Boolean {
        return _currentPage.intValue == 0
    }
}
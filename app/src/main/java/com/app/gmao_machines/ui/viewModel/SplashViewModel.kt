package com.app.gmao_machines.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _navigateToMain = MutableStateFlow(false)
    val navigateToMain: StateFlow<Boolean> = _navigateToMain.asStateFlow()

    init {
        startSplashScreen()
    }

    private fun startSplashScreen() {
        viewModelScope.launch {
            delay(4000)
            _isLoading.value = false
            _navigateToMain.value = true
        }
    }

    fun onNavigationComplete() {
        _navigateToMain.value = false
    }
}
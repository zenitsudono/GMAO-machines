package com.app.gmao_machines.ui.viewModel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")
private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")

class ThemeViewModel(private val context: Context) : ViewModel() {
    private val _isDarkTheme = mutableStateOf(false)
    val isDarkTheme: State<Boolean> = _isDarkTheme

    init {
        viewModelScope.launch {
            try {
                // Load saved theme preference
                val preferences = context.dataStore.data.first()
                _isDarkTheme.value = preferences[IS_DARK_THEME] ?: false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleTheme() {
        setTheme(!_isDarkTheme.value)
    }

    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            try {
                // Save theme preference
                context.dataStore.edit { preferences ->
                    preferences[IS_DARK_THEME] = isDark
                }
                _isDarkTheme.value = isDark
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ThemeViewModel(context) as T
        }
    }
} 
package com.app.gmao_machines.ui.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

object ThemeViewModelProvider : ViewModelStoreOwner {
    override val viewModelStore = ViewModelStore()
    @SuppressLint("StaticFieldLeak")
    private var instance: ThemeViewModel? = null

    // override fun getViewModelStore(): ViewModelStore = viewModelStore

    fun getThemeViewModel(context: Context): ThemeViewModel {
        return instance ?: ThemeViewModel(context.applicationContext).also {
            instance = it
        }
    }
} 
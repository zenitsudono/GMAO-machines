package com.app.gmao_machines.provider

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.app.gmao_machines.ui.viewModel.ThemeViewModel

object ThemeViewModelProvider : ViewModelStoreOwner {
    private val _viewModelStore = ViewModelStore()

    @SuppressLint("StaticFieldLeak")
    private var instance: ThemeViewModel? = null

    override val viewModelStore: ViewModelStore get() = _viewModelStore

    fun getThemeViewModel(context: Context): ThemeViewModel {
        return instance ?: ThemeViewModel(context.applicationContext).also {
            instance = it
        }
    }
}
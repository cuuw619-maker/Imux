package com.imux.launcher.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherSettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LauncherSettingsRepository(application)

    val settings = repository.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        LauncherSettings()
    )

    fun update(transform: (LauncherSettings) -> LauncherSettings) {
        viewModelScope.launch { repository.update(transform) }
    }
}

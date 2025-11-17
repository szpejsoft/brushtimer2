package com.szpejsoft.brushtimer2.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    data class UiState(
        val blinkEnabled: Boolean = true,
        val soundEnabled: Boolean = false,
        val timerPeriodSec: Int = 120,
        val isBusy: Boolean = false
    )

    val uiState: StateFlow<UiState>
        get() = _uiState

    private val _uiState = MutableStateFlow<UiState>(UiState())

    fun onToggleBlinkEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(blinkEnabled = enabled)
    }

    fun onToggleSoundEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(soundEnabled = enabled)
    }

    fun onTimerPeriodChanged() {
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBusy = true)
            delay(1000)
            _uiState.value = _uiState.value.copy(isBusy = false)
        }
    }

}
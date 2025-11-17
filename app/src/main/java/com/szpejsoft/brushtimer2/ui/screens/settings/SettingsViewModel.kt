package com.szpejsoft.brushtimer2.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szpejsoft.brushtimer2.common.settings.TimerSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val timerSettings: TimerSettings
) : ViewModel() {
    data class UiState(
        val blinkEnabled: Boolean = true,
        val soundEnabled: Boolean = true,
        val timerPeriodSec: Int = 120,
        val isBusy: Boolean = false
    )

    val uiState: StateFlow<UiState>
        get() = _uiState

    private val _uiState = MutableStateFlow<UiState>(UiState())

    init {
        viewModelScope.launch {
            timerSettings.soundEnabled.collect { enabled ->
                _uiState.value = _uiState.value.copy(soundEnabled = enabled)
            }
            timerSettings.blinkEnabled.collect { enabled ->
                _uiState.value = _uiState.value.copy(blinkEnabled = enabled)
            }
        }
    }

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
            val blinkEnabled = _uiState.value.blinkEnabled
            val soundEnabled = _uiState.value.soundEnabled
            val timerPeriodSec = _uiState.value.timerPeriodSec
            _uiState.value = _uiState.value.copy(isBusy = true)
            timerSettings.saveBlinkEnabled(blinkEnabled)
            timerSettings.saveSoundEnabled(soundEnabled)

            delay(1000)
            _uiState.value = _uiState.value.copy(isBusy = false)
        }
    }

}
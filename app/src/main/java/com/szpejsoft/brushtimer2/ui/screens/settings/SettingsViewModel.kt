package com.szpejsoft.brushtimer2.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szpejsoft.brushtimer2.common.combine
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
        val timerPeriod: Long = 120,
        val isBusy: Boolean = false,
    )

    val uiState: StateFlow<UiState>
        get() = _uiState

    private val _uiState = MutableStateFlow<UiState>(UiState())

    init {
        viewModelScope.launch {
            with(timerSettings) {
                combine(soundEnabled, blinkEnabled, timerDuration)
                    .collect { (soundEnabled, blinkEnabled, timerDuration) ->
                        _uiState.value = _uiState.value.copy(
                            blinkEnabled = blinkEnabled,
                            soundEnabled = soundEnabled,
                            timerPeriod = timerDuration
                        )
                    }
            }
        }
    }

    fun onToggleBlinkEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(blinkEnabled = enabled)
    }

    fun onToggleSoundEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(soundEnabled = enabled)
    }

    fun onTimerPeriodChanged(period: Long) {
        _uiState.value = _uiState.value.copy(timerPeriod = period)
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            val blinkEnabled = _uiState.value.blinkEnabled
            val soundEnabled = _uiState.value.soundEnabled
            val timerPeriodSec = _uiState.value.timerPeriod
            _uiState.value = _uiState.value.copy(isBusy = true)
            timerSettings.saveBlinkEnabled(blinkEnabled)
            timerSettings.saveSoundEnabled(soundEnabled)
            timerSettings.saveTimerDuration(timerPeriodSec)
            delay(1000)
            _uiState.value = _uiState.value.copy(isBusy = false)
        }
    }

}
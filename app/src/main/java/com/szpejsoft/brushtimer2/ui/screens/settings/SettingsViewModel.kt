package com.szpejsoft.brushtimer2.ui.screens.settings

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szpejsoft.brushtimer2.common.Constants
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
        val adaptiveColorSchemeEnabled: Boolean = true,
        val timerPeriod: Long = Constants.DEFAULT_BRUSH_TIMER_PERIOD,
        val isBusy: Boolean = false,
        val isAdaptiveColorSchemeSupported: Boolean = false
    )

    val uiState: StateFlow<UiState>
        get() = _uiState

    private val _uiState = MutableStateFlow<UiState>(UiState())

    init {
        setupAdaptiveColorsSwitchVisibility()

        viewModelScope.launch {
            with(timerSettings) {
                combine(soundEnabled, blinkEnabled, adaptiveColorSchemeEnabled, timerDuration)
                    .collect { (soundEnabled, blinkEnabled, adaptiveColorSchemeEnabled, timerDuration) ->
                        _uiState.value = _uiState.value.copy(
                            blinkEnabled = blinkEnabled,
                            soundEnabled = soundEnabled,
                            adaptiveColorSchemeEnabled = adaptiveColorSchemeEnabled,
                            timerPeriod = timerDuration
                        )
                    }
            }
        }
    }

    private fun setupAdaptiveColorsSwitchVisibility() {
        _uiState.value =
            _uiState.value.copy(isAdaptiveColorSchemeSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    }

    fun onToggleBlinkEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(blinkEnabled = enabled)
    }

    fun onToggleSoundEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(soundEnabled = enabled)
    }

    fun onToggleAdaptiveColorScheme(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(adaptiveColorSchemeEnabled = enabled)
    }

    fun onTimerPeriodChanged(period: Long) {
        _uiState.value = _uiState.value.copy(timerPeriod = period)
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            val blinkEnabled = _uiState.value.blinkEnabled
            val soundEnabled = _uiState.value.soundEnabled
            val adaptiveColorSchemeEnabled = _uiState.value.adaptiveColorSchemeEnabled
            val timerPeriodSec = _uiState.value.timerPeriod
            _uiState.value = _uiState.value.copy(isBusy = true)
            timerSettings.saveBlinkEnabled(blinkEnabled)
            timerSettings.saveSoundEnabled(soundEnabled)
            timerSettings.saveTimerDuration(timerPeriodSec)
            timerSettings.saveAdaptiveColorSchemeEnabled(adaptiveColorSchemeEnabled)
            delay(1000)
            _uiState.value = _uiState.value.copy(isBusy = false)
        }
    }

}
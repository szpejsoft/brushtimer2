package com.szpejsoft.brushtimer2.ui.screens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szpejsoft.brushtimer2.common.Constants
import com.szpejsoft.brushtimer2.common.settings.TimerSettings
import com.szpejsoft.brushtimer2.services.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TimerViewModel
@Inject
constructor(
    private val timerSettings: TimerSettings
) : ViewModel() {

    sealed interface UiState {
        val timeLeftSec: Long

        data class Idle(override val timeLeftSec: Long, val playSound: Boolean) : UiState
        data class Running(override val timeLeftSec: Long, val blink: Boolean) : UiState
    }

    val uiState: StateFlow<UiState>
        get() = _uiState
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value)

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle(Constants.DEFAULT_BRUSH_TIMER_PERIOD, false))

    private var timerService: TimerService? = null
    private var soundEnabled = false
    private var blinkEnabled = false
    private var timerDurationSec = Constants.DEFAULT_BRUSH_TIMER_PERIOD
        set(value) {
            field = value
            blinkMillis = LongArray(3) { i -> (i + 1) * value * 1000 / 4 }
            stop()
        }
    private var blinkMillis: LongArray = longArrayOf(0)

    init {
        with(viewModelScope) {
            launch {
                timerSettings.soundEnabled
                    .collect { soundEnabled = it }
            }
            launch {
                timerSettings.blinkEnabled
                    .collect { blinkEnabled = it }
            }
            launch {
                timerSettings.timerDuration
                    .collect { timerDurationSec = it }
            }
        }
    }

    fun onServiceBound(service: TimerService) {
        timerService = service
        viewModelScope.launch {
            val timerDurationMillis = timerDurationSec * 1000
            timerService?.let { service ->
                service.timeLeftMillis
                    .scan(initial = timerDurationMillis to timerDurationMillis) { acc, value -> acc.second to value }
                    .collect { (previousTimeLeftMillis, timeLeftMillis) ->
                        if (previousTimeLeftMillis < timerDurationMillis && timeLeftMillis == timerDurationMillis) {
                            //after timer stops
                            _uiState.value = UiState.Idle(timerDurationSec, soundEnabled)
                        } else if (previousTimeLeftMillis == timerDurationMillis && timeLeftMillis == 0L) { //first state before timer start
                            _uiState.value = UiState.Idle(timerDurationSec, false)
                        } else {
                            _uiState.value = UiState.Running(
                                timeLeftMillis / 1000,
                                shouldBlink(previousTimeLeftMillis, timeLeftMillis) && blinkEnabled
                            )
                        }
                    }
            }
        }
    }

    fun start() {
        timerService?.startTimer()
    }

    fun stop() {
        timerService?.stopTimer()
    }

    private fun shouldBlink(previousTimeLeft: Long, timeLeft: Long): Boolean =
        blinkMillis.any { blinkTime -> blinkTime in previousTimeLeft..<timeLeft }

}

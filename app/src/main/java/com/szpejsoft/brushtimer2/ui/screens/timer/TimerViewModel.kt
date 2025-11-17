package com.szpejsoft.brushtimer2.ui.screens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szpejsoft.brushtimer2.common.Constants
import com.szpejsoft.brushtimer2.common.settings.TimerSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    private var timerJob: Job? = null
    private var soundEnabled = false
    private var blinkEnabled = false
    private var timerDuration = Constants.DEFAULT_BRUSH_TIMER_PERIOD
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
                    .collect { timerDuration = it }
            }
        }
    }


    fun start() {
        val startTime = System.currentTimeMillis()
        var elapsedTimeMillis = 0L

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (elapsedTimeMillis < timerDuration * 1000) {
                delay(TIME_STEP_MILLIS)
                elapsedTimeMillis = System.currentTimeMillis() - startTime
                val timeLeftMillis = timerDuration * 1000 - elapsedTimeMillis
                val previousTimeLeft = _uiState.value.timeLeftSec * 1000
                _uiState.value = UiState.Running(
                    timeLeftMillis / 1000,
                    shouldBlink(previousTimeLeft, timeLeftMillis) && blinkEnabled
                )
            }
            delay(TIME_STEP_MILLIS)
            _uiState.value = UiState.Idle(timerDuration, soundEnabled)
        }
    }

    private fun shouldBlink(previousTimeLeft: Long, timeLeft: Long): Boolean =
        blinkMillis.any { blinkTime -> blinkTime in previousTimeLeft..<timeLeft }

    fun stop() {
        viewModelScope.launch {
            timerJob?.cancel()
            _uiState.value = UiState.Idle(timerDuration, false)
        }
    }

    companion object {
        private const val TIME_STEP_MILLIS = 100L
    }

}
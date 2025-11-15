package com.szpejsoft.brushtimer2.ui.screens.timer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    sealed interface UiState {
        val timeLeftSec: Long

        data class Idle(override val timeLeftSec: Long, val playSound: Boolean) : UiState
        data class Running(override val timeLeftSec: Long, val blink: Boolean) : UiState
    }

    val uiState: StateFlow<UiState>
        get() = _uiState
            // .onEach { Log.d("ptsz", "uiState: ${_uiState.value}") }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value)


    private val _uiState = MutableStateFlow<UiState>(UiState.Idle(TIMER_DURATION_MILLIS / 1000, false))

    private var timerJob: Job? = null

    init {
        Log.d("ptsz", "TVM blink: ${BLINK_MILLIS.joinToString()}")
    }


    fun start() {
        val startTime = System.currentTimeMillis()
        var elapsedTimeMillis = 0L

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (elapsedTimeMillis < TIMER_DURATION_MILLIS) {
                delay(TIME_STEP_MILLIS)
                elapsedTimeMillis = System.currentTimeMillis() - startTime
                val timeLeftMillis = TIMER_DURATION_MILLIS - elapsedTimeMillis
                val previousTimeLeft = _uiState.value.timeLeftSec * 1000
                _uiState.value = UiState.Running(timeLeftMillis / 1000, shouldBlink(previousTimeLeft, timeLeftMillis))
            }
            delay(TIME_STEP_MILLIS)
            _uiState.value = UiState.Idle(TIMER_DURATION_MILLIS / 1000, true)
        }
    }

    private fun shouldBlink(previousTimeLeft: Long, timeLeft: Long): Boolean {
        val result =  BLINK_MILLIS.any { blinkTime -> previousTimeLeft <= blinkTime && blinkTime < timeLeft }
        Log.d("ptsz", "VM shouldBlink prev: $previousTimeLeft, timeLeft: $timeLeft -> $result")
        return result
    }


    fun stop() {
        viewModelScope.launch {
            timerJob?.cancel()
            _uiState.value = UiState.Idle(TIMER_DURATION_MILLIS / 1000, false)
        }
    }

    companion object {
        //        private const val TIMER_DURATION_MILLIS = 2 * 60 * 1000L //2 min
        private const val TIMER_DURATION_MILLIS = 12 * 1000L //2 min
        private val BLINK_MILLIS = LongArray(3) { i -> (i + 1) * TIMER_DURATION_MILLIS / 4 }
        private const val TIME_STEP_MILLIS = 100L
    }

}
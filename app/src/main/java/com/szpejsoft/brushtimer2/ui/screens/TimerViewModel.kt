package com.szpejsoft.brushtimer2.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szpejsoft.brushtimer2.ui.screens.TimerViewModel.UiState.Idle
import com.szpejsoft.brushtimer2.ui.screens.TimerViewModel.UiState.Running
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class TimerViewModel : ViewModel() {

    sealed interface UiState {
        val timeLeft: String

        data class Idle(override val timeLeft: String, val playSound: Boolean) : UiState
        data class Running(override val timeLeft: String) : UiState
    }

    val uiState get() = _uiState.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle(format(TIMER_DURATION_MS), false))

    private var timerJob: Job? = null

    fun start() {
        val startTime = System.currentTimeMillis()
        var elapsedTime = 0L
        timerJob = viewModelScope.launch {
            while (elapsedTime < TIMER_DURATION_MS) {
                delay(10L)
                elapsedTime = System.currentTimeMillis() - startTime
                val timeLeft = TIMER_DURATION_MS - elapsedTime
                _uiState.value = Running(format(timeLeft))
            }
            delay(10L)
            _uiState.value = Idle(format(TIMER_DURATION_MS), true)
        }
    }

    private fun format(timeLeftMs: Long): String {
        val seconds = (timeLeftMs / 1000) % 60
        val minutes = (timeLeftMs / (1000 * 60)) % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    fun stop() {
        viewModelScope.launch {
            timerJob?.cancel()

            _uiState.value = Idle(format(TIMER_DURATION_MS), false)
        }
    }

    companion object {
        const val TIMER_DURATION_MS = 2 * 60 * 1000L //2 min
    }

}
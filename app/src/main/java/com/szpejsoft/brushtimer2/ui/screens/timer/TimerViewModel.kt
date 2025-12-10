package com.szpejsoft.brushtimer2.ui.screens.timer

import android.annotation.SuppressLint
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

    @SuppressLint("StaticFieldLeak")
    /*
    * field nulled in onCleared() method 
    */
    private var timerService: TimerService? = null

    private var soundEnabled = false
    private var blinkEnabled = false

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
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerService = null
    }

    fun onServiceBound(service: TimerService) {
        timerService = service
        viewModelScope.launch {
            timerService?.let { service ->
                service.timerStateFlow
                    .collect { state ->
                        _uiState.value = if (state.isRunning) {
                            UiState.Running(
                                timeLeftSec = state.timeLeftMillis / 1000,
                                blink = state.quarterPassed && blinkEnabled
                            )
                        } else {
                            UiState.Idle(
                                timeLeftSec = state.timeLeftMillis / 1000,
                                playSound = state.quarterPassed && soundEnabled
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
}

package com.szpejsoft.brushtimer2.ui.screens.main

import androidx.lifecycle.ViewModel
import com.szpejsoft.brushtimer2.common.settings.TimerSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel
@Inject
constructor(timerSettings: TimerSettings) : ViewModel() {

    val adaptiveColorSchemeEnabled = timerSettings.adaptiveColorSchemeEnabled

}
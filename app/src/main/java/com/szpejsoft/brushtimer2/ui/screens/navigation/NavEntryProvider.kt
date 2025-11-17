package com.szpejsoft.brushtimer2.ui.screens.navigation

import androidx.navigation3.runtime.NavEntry
import com.szpejsoft.brushtimer2.ui.screens.settings.SettingsScreen
import com.szpejsoft.brushtimer2.ui.screens.timer.TimerScreen

typealias NavEntryProvider = (Screen) -> NavEntry<Screen>

val entryProvider: NavEntryProvider = { key ->
    when (key) {
        is Screen.Settings -> NavEntry(key) {
            SettingsScreen()
        }

        is Screen.Timer -> NavEntry(key) {
            TimerScreen()
        }
    }
}
package com.szpejsoft.brushtimer2.ui.screens.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import com.szpejsoft.brushtimer2.ui.screens.settings.SettingsScreen
import com.szpejsoft.brushtimer2.ui.screens.timer.TimerScreen

typealias NavEntryProvider = (Screen) -> NavEntry<Screen>

val entryProvider: NavEntryProvider = entryProvider {
    entry<Screen.Settings> { SettingsScreen() }
    entry<Screen.Timer> { TimerScreen() }
}
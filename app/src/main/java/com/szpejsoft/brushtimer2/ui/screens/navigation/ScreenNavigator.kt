package com.szpejsoft.brushtimer2.ui.screens.navigation

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow

class ScreenNavigator {

    val currentBottomTab = MutableStateFlow<BottomTab>(BottomTab.Timer)
    val backStack = mutableStateListOf<Screen>(Screen.Timer)

    fun navigateBack() {
        if (backStack.last() == Screen.Settings) {
            navigateBottomTab(BottomTab.Timer)
        } else {
            backStack.removeLastOrNull()
        }
    }

    fun navigateBottomTab(tab: BottomTab) {
        currentBottomTab.value = tab
        backStack.clear()
        val screen = when (tab) {
            BottomTab.Timer -> Screen.Timer
            BottomTab.Settings -> Screen.Settings
        }
        backStack.add(screen)
    }

    companion object {
        val BOTTOM_TABS = listOf(BottomTab.Timer, BottomTab.Settings)
    }

}
package com.szpejsoft.brushtimer2.ui.screens.navigation

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow

class ScreenNavigator {

    val currentBottomTab = MutableStateFlow<Screen>(Screen.Timer)
    val backStack = mutableStateListOf<Screen>(Screen.Timer)

    fun navigateBack() {
        if (backStack.last() == Screen.Settings) {
            navigateBottomTab(Screen.Timer)
        } else {
            backStack.removeLastOrNull()
        }
    }

    fun navigateBottomTab(screen: Screen) {
        currentBottomTab.value = screen
        backStack.clear()
        backStack.add(screen)
    }

    companion object {
        val BOTTOM_TABS = listOf(Screen.Timer, Screen.Settings)
    }

}

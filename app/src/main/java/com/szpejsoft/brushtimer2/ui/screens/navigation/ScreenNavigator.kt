package com.szpejsoft.brushtimer2.ui.screens.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
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
        /**
         * A Saver for the ScreenNavigator.
         * It saves the entire backstack as a list of Screens.
         * The first item in the list is considered the current bottom tab.
         */
        val Saver = listSaver<ScreenNavigator, Screen>(
            save = { navigator ->
                // The object to save is simply the list of screens in the backstack
                navigator.backStack.toList()
            },
            restore = { savedList ->
                // When restoring, create a new navigator
                val navigator = ScreenNavigator()
                // The first item saved was the current tab, which is also the root of the backstack
                val currentTab = savedList.firstOrNull() ?: Screen.Timer
                navigator.currentBottomTab.value = currentTab
                // Clear the default backstack and restore it from the saved list
                navigator.backStack.clear()
                navigator.backStack.addAll(savedList)
                navigator
            }
        )
    }

}

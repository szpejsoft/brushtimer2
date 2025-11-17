package com.szpejsoft.brushtimer2.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.NavDisplay
import com.szpejsoft.brushtimer2.ui.screens.navigation.BottomBar
import com.szpejsoft.brushtimer2.ui.screens.navigation.ScreenNavigator
import com.szpejsoft.brushtimer2.ui.screens.navigation.entryProvider

@Composable
fun MainScreen() {
    val screenNavigator = remember { ScreenNavigator() }
    val currentBottomTab = screenNavigator.currentBottomTab.collectAsState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        content = { innerPadding -> MainScreenContent(innerPadding, screenNavigator) },
        bottomBar = {
            BottomAppBar(modifier = Modifier) {
                BottomBar(
                    bottomTabs = ScreenNavigator.BOTTOM_TABS,
                    currentTab = currentBottomTab.value,
                    onTabSelected = { tab -> screenNavigator.navigateBottomTab(tab) }

                )
            }
        }
    )
}

@Composable
fun MainScreenContent(
    padding: PaddingValues,
    screenNavigator: ScreenNavigator
) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        NavDisplay(
            backStack = screenNavigator.backStack,
            entryProvider = entryProvider,
            onBack = { screenNavigator.navigateBack() }
        )
    }
}

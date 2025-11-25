package com.szpejsoft.brushtimer2.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.ui.NavDisplay
import com.szpejsoft.brushtimer2.ui.screens.navigation.BottomBar
import com.szpejsoft.brushtimer2.ui.screens.navigation.ScreenNavigator
import com.szpejsoft.brushtimer2.ui.screens.navigation.entryProvider
import com.szpejsoft.brushtimer2.ui.theme.Brushtimer2Theme

@Composable
fun MainScreen(
    mainScreenViewModel: MainScreenViewModel = hiltViewModel()
) {
    val screenNavigator = rememberSaveable(saver = ScreenNavigator.Saver) { ScreenNavigator() }
    val currentBottomTab = screenNavigator.currentBottomTab.collectAsState()
    val adaptiveColorSchemeEnabled by mainScreenViewModel.adaptiveColorSchemeEnabled.collectAsState()

    Brushtimer2Theme(
        dynamicColor = adaptiveColorSchemeEnabled
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            content = { innerPadding -> MainScreenContent(innerPadding, screenNavigator) },
            bottomBar = {
                BottomAppBar(modifier = Modifier) {
                    BottomBar(
                        currentScreen = currentBottomTab.value,
                        onTabSelected = { tab -> screenNavigator.navigateBottomTab(tab) }
                    )
                }
            }
        )
    }
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

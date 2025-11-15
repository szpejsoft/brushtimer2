package com.szpejsoft.brushtimer2.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.szpejsoft.brushtimer2.ui.screens.timer.TimerScreen

@Composable
fun MainScreen() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        content = { innerPadding -> MainScreenContent(innerPadding) }
    )
}

@Composable
fun MainScreenContent(padding: PaddingValues) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        TimerScreen()
    }
}
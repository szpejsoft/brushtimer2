package com.szpejsoft.brushtimer2

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.szpejsoft.brushtimer2.ui.screens.timer.NarrowScreen
import com.szpejsoft.brushtimer2.ui.screens.timer.TimerViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TimerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Your tests will go here

    @Test
    fun whenStateIsIdle_StartButtonIsEnabled_StopButtonIsDisabled() {
        val state = TimerViewModel.UiState.Idle(120, false)
        composeTestRule.setContent {
            NarrowScreen(uiState = state, onStartButtonClicked = {}, onStopButtonClicked = {})
        }


    }

}
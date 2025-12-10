package com.szpejsoft.brushtimer2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.szpejsoft.brushtimer2.ui.screens.timer.NarrowScreen
import com.szpejsoft.brushtimer2.ui.screens.timer.TimerTestTags.START_BUTTON
import com.szpejsoft.brushtimer2.ui.screens.timer.TimerTestTags.STOP_BUTTON
import com.szpejsoft.brushtimer2.ui.screens.timer.TimerTestTags.TIMER_TEXT
import com.szpejsoft.brushtimer2.ui.screens.timer.TimerViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TimerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenStateIsIdle_StartButtonIsEnabled_And_StopButtonIsDisabled() {
        //arrange
        val idleState = TimerViewModel.UiState.Idle(timeLeftSec = 120, playSound = false)

        //act
        composeTestRule.setContent {
            NarrowScreen(
                uiState = idleState,
                onStartButtonClicked = { },
                onStopButtonClicked = { }
            )
        }

        //assert
        with(composeTestRule.onNodeWithTag(TIMER_TEXT)) {
            assertIsDisplayed()
            assertTextEquals("02:00")
        }

        with(composeTestRule.onNodeWithTag(START_BUTTON)) {
            assertIsDisplayed()
            assertIsEnabled()
        }
        with(composeTestRule.onNodeWithTag(STOP_BUTTON)) {
            assertIsDisplayed()
            assertIsNotEnabled()
        }

    }

    @Test
    fun whenStateIsRunning_StopButtonIsEnabled_And_StartButtonIsDisabled() {
        //arrange
        val runningState = TimerViewModel.UiState.Running(timeLeftSec = 90, blink = false)

        //act
        composeTestRule.setContent {
            NarrowScreen(
                uiState = runningState,
                onStartButtonClicked = { },
                onStopButtonClicked = { }
            )
        }

        //assert
        with(composeTestRule.onNodeWithTag(TIMER_TEXT)) {
            assertIsDisplayed()
            assertTextEquals("01:30")
        }

        with(composeTestRule.onNodeWithTag(START_BUTTON)) {
            assertIsDisplayed()
            assertIsNotEnabled()
        }

        with(composeTestRule.onNodeWithTag(STOP_BUTTON)) {
            assertIsDisplayed()
            assertIsEnabled()
        }
    }
}


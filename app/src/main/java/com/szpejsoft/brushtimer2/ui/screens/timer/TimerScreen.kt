package com.szpejsoft.brushtimer2.ui.screens.timer

import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.szpejsoft.brushtimer2.ui.common.secToMinSec
import com.szpejsoft.brushtimer2.ui.shapes.BottomButtonShape
import com.szpejsoft.brushtimer2.ui.shapes.LeftButtonShape
import com.szpejsoft.brushtimer2.ui.shapes.RightButtonShape
import com.szpejsoft.brushtimer2.ui.shapes.TopButtonShape

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()

    val playSound = (state as? TimerViewModel.UiState.Idle)?.playSound == true
    val blink = (state as? TimerViewModel.UiState.Running)?.blink == true

    if (playSound) {
        val context = LocalContext.current
        DisposableEffect(Unit) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mediaPlayer = MediaPlayer.create(context, soundUri)
            mediaPlayer.start()
            onDispose { mediaPlayer.release() }
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (blink) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing),
        label = "blinkAnimation"
    )
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .background(Color.White.copy(alpha = alpha))
            .padding(12.dp)
    ) {
        if (windowSizeClass.isWidthAtLeastBreakpoint(600)) {
            WideScreen(state, { viewModel.start() }, { viewModel.stop() })
        } else {
            NarrowScreen(state, { viewModel.start() }, { viewModel.stop() })
        }
    }

}

@Composable
fun WideScreen(
    uiState: TimerViewModel.UiState,
    onStartButtonClicked: () -> Unit,
    onStopButtonClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1.0f))
        Text(
            modifier = Modifier.testTag(TimerTestTags.TIMER_TEXT),
            text = secToMinSec(uiState.timeLeftSec),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.weight(1.0f))
        VerticalButtons(
            modifier = Modifier.weight(1.0f),
            isStartEnabled = uiState is TimerViewModel.UiState.Idle,
            isStopEnabled = uiState is TimerViewModel.UiState.Running,
            onStartButtonClicked = onStartButtonClicked,
            onStopButtonClicked = onStopButtonClicked
        )
    }
}

@Composable
fun NarrowScreen(
    uiState: TimerViewModel.UiState,
    onStartButtonClicked: () -> Unit,
    onStopButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1.0f))
        Text(
            modifier = Modifier.testTag(TimerTestTags.TIMER_TEXT),
            text = secToMinSec(uiState.timeLeftSec),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.weight(1.0f))
        HorizontalButtons(
            isStartEnabled = uiState is TimerViewModel.UiState.Idle,
            isStopEnabled = uiState is TimerViewModel.UiState.Running,
            onStartButtonClicked = onStartButtonClicked,
            onStopButtonClicked = onStopButtonClicked
        )
    }
}

@Composable
private fun HorizontalButtons(
    isStartEnabled: Boolean,
    isStopEnabled: Boolean,
    onStartButtonClicked: () -> Unit,
    onStopButtonClicked: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier
                .weight(1.0f)
                .testTag(TimerTestTags.START_BUTTON)
                .height(64.dp),
            shape = LeftButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = isStartEnabled,
            onClick = { onStartButtonClicked() }
        ) {
            Text(
                text = "Start",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        Spacer(modifier = Modifier.padding(2.dp))
        Button(
            modifier = Modifier
                .weight(1.0f)
                .testTag(TimerTestTags.STOP_BUTTON)
                .height(64.dp),
            shape = RightButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = isStopEnabled,
            onClick = { onStopButtonClicked() }
        ) {
            Text(
                text = "Stop",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Composable
fun VerticalButtons(
    modifier: Modifier = Modifier,
    isStartEnabled: Boolean,
    isStopEnabled: Boolean,
    onStartButtonClicked: () -> Unit,
    onStopButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .then(modifier)
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TimerTestTags.START_BUTTON)
                .weight(1.0f),
            shape = TopButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = isStartEnabled,
            onClick = { onStartButtonClicked() }
        ) {
            Text(
                text = "Start",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        Spacer(modifier = Modifier.padding(2.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TimerTestTags.STOP_BUTTON)
                .weight(1.0f),
            shape = BottomButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = isStopEnabled,
            onClick = { onStopButtonClicked() }
        ) {
            Text(
                text = "Stop",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

object TimerTestTags {
    const val TIMER_TEXT = "TimerText"
    const val START_BUTTON = "StartButton"
    const val STOP_BUTTON = "StopButton"
}
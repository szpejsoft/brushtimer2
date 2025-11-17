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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.szpejsoft.brushtimer2.ui.shapes.LeftButtonShape
import com.szpejsoft.brushtimer2.ui.shapes.RightButtonShape
import java.util.Locale

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState()

    val playSound = (state.value as? TimerViewModel.UiState.Idle)?.playSound == true
    val blink = (state.value as? TimerViewModel.UiState.Running)?.blink == true

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .background(Color.White.copy(alpha = alpha))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1.0f))
            Text(
                text = format(state.value.timeLeftSec),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.weight(1.0f))
            Buttons(
                state.value is TimerViewModel.UiState.Idle,
                state.value is TimerViewModel.UiState.Running,
                { viewModel.start() },
                { viewModel.stop() })
        }
    }
}

@Composable
private fun Buttons(
    isStartEnabled: Boolean,
    isStopEnabled: Boolean,
    onStartButtonClicked: () -> Unit,
    onStopButtonClicked: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier
                .weight(1.0f)
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

//assumption - less than an hour
private fun format(timeLeftSec: Long): String {
    val seconds = timeLeftSec % 60
    val minutes = (timeLeftSec / 60)
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
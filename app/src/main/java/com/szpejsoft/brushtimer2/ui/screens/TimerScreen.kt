package com.szpejsoft.brushtimer2.ui.screens

import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.szpejsoft.brushtimer2.ui.shapes.LeftButtonShape
import com.szpejsoft.brushtimer2.ui.shapes.RightButtonShape

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState()
    val playSound = (state.value as? TimerViewModel.UiState.Idle)?.playSound == true

    if (playSound) {
        val context = LocalContext.current
        DisposableEffect(Unit) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mediaPlayer = MediaPlayer.create(context, soundUri)
            mediaPlayer.start()
            onDispose { mediaPlayer.release() }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1.0f))
        Text(
            text = state.value.timeLeft,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.weight(1.0f))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .weight(1.0f)
                    .height(64.dp),
                shape = LeftButtonShape,
                colors = ButtonDefaults.buttonColors(
                    // Set the background color of the button
                    containerColor = MaterialTheme.colorScheme.primary,
                    // This color is used for the text and the ripple effect
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = state.value is TimerViewModel.UiState.Idle,
                onClick = { viewModel.start() }
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
                    // Set the background color of the button
                    containerColor = MaterialTheme.colorScheme.primary,
                    // This color is used for the text and the ripple effect
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = state.value is TimerViewModel.UiState.Running,

                onClick = { viewModel.stop() }
            ) {
                Text(
                    text = "Stop",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }
    }
}
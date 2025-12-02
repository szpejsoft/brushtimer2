package com.szpejsoft.brushtimer2.ui.screens.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.szpejsoft.brushtimer2.services.TimerService
import com.szpejsoft.brushtimer2.ui.common.secToMinSec
import com.szpejsoft.brushtimer2.ui.shapes.BottomButtonShape
import com.szpejsoft.brushtimer2.ui.shapes.LeftButtonShape
import com.szpejsoft.brushtimer2.ui.shapes.RightButtonShape
import com.szpejsoft.brushtimer2.ui.shapes.TopButtonShape

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isBound by remember { mutableStateOf(false) }
    val state by viewModel.uiState.collectAsState()

    val playSound = (state as? TimerViewModel.UiState.Idle)?.playSound == true
    val blink = (state as? TimerViewModel.UiState.Running)?.blink == true


    DisposableEffect(Unit) {
        val serviceIntent = Intent(context, TimerService::class.java)
        context.startService(serviceIntent) // Start the service to keep it alive

        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as TimerService.LocalBinder
                viewModel.onServiceBound(binder.getService())
                isBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
            }
        }

        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

        onDispose {
            context.unbindService(connection)
        }
    }


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

    if (isBound) {
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
    } else {
        Text(text = "Service not bound")
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


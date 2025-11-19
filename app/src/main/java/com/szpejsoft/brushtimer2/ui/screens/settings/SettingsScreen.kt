package com.szpejsoft.brushtimer2.ui.screens.settings

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.szpejsoft.brushtimer2.R
import com.szpejsoft.brushtimer2.common.Constants.BRUSH_TIMER_PERIODS
import com.szpejsoft.brushtimer2.ui.common.secToMinSec
import com.szpejsoft.brushtimer2.ui.shapes.ButtonShape

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
        ) {
            SwitchRow(
                title = stringResource(R.string.settings_screen_blink_toggle_title),
                enabled = uiState.blinkEnabled,
                onCheckedChange = { settingsViewModel.onToggleBlinkEnabled(it) }
            )
            SwitchRow(
                title = stringResource(R.string.settings_screen_sound_toggle_title),
                enabled = uiState.soundEnabled,
                onCheckedChange = { settingsViewModel.onToggleSoundEnabled(it) }
            )

            PeriodPicker(
                period = secToMinSec(uiState.timerPeriod)
            ) { period -> settingsViewModel.onTimerPeriodChanged(period) }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = !uiState.isBusy,
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = { settingsViewModel.onSaveClicked() }
            ) {
                Text(
                    text = stringResource(R.string.settings_screen_save_button),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }

        if (uiState.isBusy) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    trackColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            }
        }
    }
}

@Composable
fun SwitchRow(
    title: String,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
        Switch(
            modifier = Modifier.height(24.dp),
            checked = enabled,
            onCheckedChange = { onCheckedChange(it) },
            thumbContent = {
                if (enabled) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            }
        )
    }
}

@Composable
fun PeriodPicker(
    period: String,
    onPeriodChanged: (Long) -> Unit
) {
    val isDropDownExpanded = remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isDropDownExpanded.value) 180.0f else 0.0f,
        visibilityThreshold = 0.0f,
        animationSpec = TweenSpec(easing = LinearOutSlowInEasing),
        label = "arrowRotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_screen_brush_period_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.weight(1.0f))
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.TopEnd
        ) {
            Row(
                modifier = Modifier
                    .clickable { isDropDownExpanded.value = true }
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = period,
                    style = MaterialTheme.typography.titleLarge,
                )
                Image(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .rotate(rotationAngle)
                )
            }
            PeriodsDropDown(isDropDownExpanded.value, onPeriodChanged) { isDropDownExpanded.value = false }
        }
    }
}

@Composable
private fun PeriodsDropDown(
    expanded: Boolean,
    onPeriodChanged: (Long) -> Unit,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer),
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        BRUSH_TIMER_PERIODS.forEach { period ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = secToMinSec(period),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                },
                onClick = {
                    onDismissRequest()
                    onPeriodChanged(period)
                }
            )
        }
    }
}
package com.szpejsoft.brushtimer2.ui.screens.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.szpejsoft.brushtimer2.R

@Composable
fun BottomBar(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == Screen.Timer,
            onClick = { onTabSelected(Screen.Timer) },
            icon = { Icon(Icons.Rounded.Timer, contentDescription = stringResource(R.string.bottom_tab_timer_title)) },
            label = { Text(stringResource(R.string.bottom_tab_timer_title)) }
        )
        NavigationBarItem(
            selected = currentScreen == Screen.Settings,
            onClick = { onTabSelected(Screen.Settings) },
            icon = {
                Icon(
                    Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.bottom_tab_settings_title)
                )
            },
            label = { Text(stringResource(R.string.bottom_tab_settings_title)) }
        )
    }
}


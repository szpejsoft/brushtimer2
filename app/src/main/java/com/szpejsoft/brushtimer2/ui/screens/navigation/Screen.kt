package com.szpejsoft.brushtimer2.ui.screens.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import com.szpejsoft.brushtimer2.R


sealed class Screen(val imageVector: ImageVector, @field:StringRes val title: Int) {
    data object Timer : Screen(Icons.Rounded.Timer, R.string.bottom_tab_timer_title)
    data object Settings : Screen(Icons.Rounded.Settings, R.string.bottom_tab_settings_title)
}
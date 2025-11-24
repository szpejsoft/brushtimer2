package com.szpejsoft.brushtimer2.ui.screens.navigation

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
sealed class Screen : Parcelable {
    @Parcelize
    data object Timer : Screen()
    @Parcelize
    data object Settings : Screen()
}
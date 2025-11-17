package com.szpejsoft.brushtimer2.ui.screens.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    object Timer : Screen()

    @Serializable
    object Settings : Screen()
}
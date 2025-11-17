package com.szpejsoft.brushtimer2.ui.common

import java.util.Locale

//assumption - less than an hour
fun secToMinSec(timeLeftSec: Long): String {
    val seconds = timeLeftSec % 60
    val minutes = (timeLeftSec / 60)
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
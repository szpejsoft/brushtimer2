package com.szpejsoft.brushtimer2.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.szpejsoft.brushtimer2.ui.screens.MainScreen
import com.szpejsoft.brushtimer2.ui.theme.Brushtimer2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brushtimer2Theme {
                MainScreen()
            }
        }
    }
}
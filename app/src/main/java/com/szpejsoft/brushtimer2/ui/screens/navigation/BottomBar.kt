package com.szpejsoft.brushtimer2.ui.screens.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun BottomBar(
    bottomTabs: List<BottomTab>,
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar {
        bottomTabs.forEach { tab ->
            NavigationBarItem(
                alwaysShowLabel = true,
                icon = { Icon(tab.imageVector, contentDescription = stringResource(tab.title)) },
                label = { Text(stringResource(tab.title)) },
                selected = tab == currentTab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}


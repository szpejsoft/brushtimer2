package com.szpejsoft.brushtimer2.common.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.szpejsoft.brushtimer2.common.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject


class TimerSettings
@Inject constructor(context: Context) {

    val blinkEnabled: Flow<Boolean>
        get() = dataStore.data
            .map { preferences -> preferences[KEY_BLINK_ENABLED] ?: Constants.DEFAULT_BLINK_ENABLED }
            .shareIn(scope = scope, started = SharingStarted.WhileSubscribed())

    val soundEnabled: Flow<Boolean>
        get() = dataStore.data
            .map { preferences -> preferences[KEY_SOUND_ENABLED] ?: Constants.DEFAULT_SOUND_ENABLED }
            .shareIn(scope = scope, started = SharingStarted.WhileSubscribed())

    val timerDuration: Flow<Long>
        get() = dataStore.data
            .map { preferences -> preferences[KEY_TIMER_DURATION] ?: Constants.DEFAULT_BRUSH_TIMER_PERIOD }
            .shareIn(scope = scope, started = SharingStarted.WhileSubscribed())

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME, scope = scope)
    private val dataStore = context.dataStore

    suspend fun saveBlinkEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_BLINK_ENABLED] = enabled
        }
    }

    suspend fun saveSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SOUND_ENABLED] = enabled
        }
    }

    suspend fun saveTimerDuration(duration: Long) {
        dataStore.edit { preferences ->
            preferences[KEY_TIMER_DURATION] = duration
        }
    }

    companion object {
        private const val PREFS_NAME = "timer_settings"
        private val KEY_TIMER_DURATION = longPreferencesKey("timer_duration")
        private val KEY_BLINK_ENABLED = booleanPreferencesKey("blink_enabled")
        private val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    }

}
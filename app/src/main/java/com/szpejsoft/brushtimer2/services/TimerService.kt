package com.szpejsoft.brushtimer2.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.szpejsoft.brushtimer2.R
import com.szpejsoft.brushtimer2.common.Constants
import com.szpejsoft.brushtimer2.common.settings.TimerSettings
import com.szpejsoft.brushtimer2.ui.MainActivity
import com.szpejsoft.brushtimer2.ui.common.secToMinSec
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    data class TimerState(
        val timeLeftMillis: Long,
        val quarterPassed: Boolean,
        val isRunning: Boolean
    )

    val timerStateFlow: StateFlow<TimerState> get() = _timerStateFlow

    @Inject
    lateinit var settings: TimerSettings

    private val stopActionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_STOP) {
                stopTimer()
            }
        }
    }

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    private var timerJob: Job? = null
    private var timerDurationMillis: Long = Constants.DEFAULT_BRUSH_TIMER_PERIOD * 1000
        set(value) {
            field = value
            quarterTimes = LongArray(3) { i -> (i + 1) * value / 4 }
            timerJob?.cancel()
            _timerStateFlow.value = TimerState(timeLeftMillis = value, quarterPassed = false, isRunning = false)
        }
    private val _timerStateFlow = MutableStateFlow(
        TimerState(
            timeLeftMillis = timerDurationMillis,
            quarterPassed = false,
            isRunning = false
        )
    )
    private var quarterTimes: LongArray = longArrayOf(0)
    private lateinit var notificationManager: NotificationManager

    init {
        serviceScope.launch {
            settings.timerDuration.collect {
                timerDurationMillis = it * 1000
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        setupNotification()
        registerStopButtonReceiver()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        unregisterReceiver(stopActionReceiver)
        super.onDestroy()
    }

    fun startTimer() {
        val startTime = System.currentTimeMillis()
        var elapsedTimeMillis = 0L
        _timerStateFlow.value = TimerState(
            timeLeftMillis = timerDurationMillis,
            quarterPassed = false,
            isRunning = false
        )
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            startForeground(
                NOTIFICATION_ID,
                createNotification(secToMinSec(_timerStateFlow.value.timeLeftMillis))
            )
            while (elapsedTimeMillis < timerDurationMillis) {
                delay(TIME_STEP_MILLIS)
                elapsedTimeMillis = System.currentTimeMillis() - startTime
                val timeLeftMillis = timerDurationMillis - elapsedTimeMillis
                val previousTimeLeftMillis = _timerStateFlow.value.timeLeftMillis
                val quarterPassed = quarterTimePassed(previousTimeLeftMillis, timeLeftMillis)
                _timerStateFlow.value = TimerState(timeLeftMillis, quarterPassed, true)
                val notification = createNotification(secToMinSec(timeLeftMillis / 1000))
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
            delay(TIME_STEP_MILLIS)
            _timerStateFlow.value = TimerState(
                timeLeftMillis = timerDurationMillis,
                quarterPassed = false,
                isRunning = false
            )
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerStateFlow.value = TimerState(
            timeLeftMillis = timerDurationMillis,
            quarterPassed = false,
            isRunning = false
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(timeLeft: String): Notification {
        val notificationView = RemoteViews(packageName, R.layout.notification).apply {
            setTextViewText(R.id.timer, getString(R.string.time_left, timeLeft))
            setOnClickPendingIntent(R.id.stopButton, getStopActionPendingIntent())
        }
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(notificationView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setContentIntent(getMainActivityPendingIntent())
            .build()
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    private fun registerStopButtonReceiver() {
        ContextCompat.registerReceiver(
            this,
            stopActionReceiver,
            IntentFilter(ACTION_STOP),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun setupNotification() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun getStopActionPendingIntent(): PendingIntent {
        val stopIntent = Intent(ACTION_STOP).setPackage(packageName)
        return PendingIntent.getBroadcast(this, ACTION_STOP_REQUEST_CODE, stopIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun quarterTimePassed(previousTimeLeft: Long, timeLeft: Long): Boolean =
        quarterTimes.any { quarterTime -> quarterTime in timeLeft..<previousTimeLeft }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_STOP = "com.szpejsoft.brushtimer2.STOP_TIMER"
        const val ACTION_STOP_REQUEST_CODE = 42
        private const val TIME_STEP_MILLIS = 48L
    }

}

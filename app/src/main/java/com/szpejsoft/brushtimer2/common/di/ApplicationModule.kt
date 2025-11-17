package com.szpejsoft.brushtimer2.common.di

import android.app.Application
import com.szpejsoft.brushtimer2.common.settings.TimerSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun timerSettings(application: Application): TimerSettings = TimerSettings(application)

}

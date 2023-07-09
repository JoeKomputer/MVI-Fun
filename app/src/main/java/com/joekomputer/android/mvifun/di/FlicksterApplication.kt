package com.joekomputer.android.mvifun.di

import android.app.Application
import com.joekomputer.android.mvifun.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class FlicksterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // TODO(Timber): plant release tree
        }
    }
}

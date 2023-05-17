package com.urbn.android.flickster.di

import android.app.Application
import com.urbn.android.flickster.BuildConfig
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

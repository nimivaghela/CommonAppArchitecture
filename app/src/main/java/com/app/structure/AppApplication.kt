package com.app.structure

import androidx.multidex.MultiDexApplication
import com.app.structure.injection.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class AppApplication : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AppApplication)
            modules(appModules)
        }
    }
}
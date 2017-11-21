package com.amandariu.tagger.demo

import android.app.Application
import android.os.Build
import android.os.StrictMode

import com.amandariu.tagger.BuildConfig
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

/**
 * @author Amanda Riu
 */
class TaggerApplication : Application() {
    private var mRefWatcher: RefWatcher? = null

    override fun onCreate() {
        super.onCreate()

        instance = applicationContext as TaggerApplication
        //
        // Install LeakCanary for monitoring leaks
        // https://github.com/square/leakcanary
        //
        mRefWatcher = LeakCanary.install(this)
        //
        // Enable Strict Mode if we are in debug mode - this will give us additional
        // feedback if we are doing something we're not supposed to be doing.
        //
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder()
                            .detectDiskWrites()
                            .detectDiskReads()
                            .detectNetwork()
                            .penaltyLog()
                            .build()
            )

            val vmBuilder = StrictMode.VmPolicy.Builder()
            vmBuilder
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .detectLeakedRegistrationObjects()
                    .detectLeakedSqlLiteObjects()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                vmBuilder.detectFileUriExposure()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                vmBuilder.detectCleartextNetwork()
            }

            StrictMode.setVmPolicy(
                    vmBuilder.build()
            )
        }
    }

    companion object {
        var instance: TaggerApplication? = null
            private set

        @Suppress("UNUSED")
        val refWatcher: RefWatcher?
            get() = TaggerApplication.instance!!.mRefWatcher
    }
}

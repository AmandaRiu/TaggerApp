package com.amandariu.tagger.demo;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.amandariu.tagger.BuildConfig;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * @author amandariu (11/5/17)
 */
public class TaggerApplication extends Application {

    private static TaggerApplication sInstance;
    private RefWatcher mRefWatcher;

    public static TaggerApplication getInstance() {
        return sInstance;
    }

    public static RefWatcher getRefWatcher() {
        return TaggerApplication.getInstance().mRefWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = (TaggerApplication) getApplicationContext();
        //
        // Install LeakCanary for monitoring leaks
        // https://github.com/square/leakcanary
        //
        mRefWatcher = LeakCanary.install(this);
        //
        // Enable Strict Mode if we are in debug mode - this will give us additional
        // feedback if we are doing something we're not supposed to be doing.
        //
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            .detectDiskWrites()
                            .detectDiskReads()
                            .detectNetwork()
                            .penaltyLog()
                            .build()
            );

            StrictMode.VmPolicy.Builder vmBuilder = new StrictMode.VmPolicy.Builder();
            vmBuilder
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .detectLeakedRegistrationObjects()
                    .detectLeakedSqlLiteObjects();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                vmBuilder.detectFileUriExposure();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                vmBuilder.detectCleartextNetwork();
            }

            StrictMode.setVmPolicy(
                    vmBuilder.build()
            );
        }
    }
}

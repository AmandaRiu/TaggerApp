package com.amandariu.tagger.demo.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.amandariu.tagger.demo.TaggerApplication;

/**
 * Miscellaneous network utilities.
 *
 * @author Amanda Riu
 */
public abstract class NetworkUtils {

    /**
     * Checks if the device is connected to a network.
     * @return True if the device has a network connection, else false.
     */
    public static boolean isNetworkConnected() {
        ConnectivityManager mgr;
        Context context = TaggerApplication.getInstance().getApplicationContext();
        mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mgr.getActiveNetworkInfo();
        return networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected();
    }
}

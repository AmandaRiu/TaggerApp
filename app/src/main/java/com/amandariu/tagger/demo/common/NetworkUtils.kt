package com.amandariu.tagger.demo.common

import android.content.Context
import android.net.ConnectivityManager

/**
 * Checks if the device is connected to a network.
 * @return True if the device has a network connection, else false.
 */
fun isNetworkConnected(context: Context): Boolean {
    val mgr: ConnectivityManager
    mgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = mgr.activeNetworkInfo
    return (networkInfo != null
            && networkInfo.isAvailable
            && networkInfo.isConnected)
}
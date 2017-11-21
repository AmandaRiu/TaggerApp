package com.amandariu.tagger.demo.utils

import android.os.Build

/**
 * Verifies the minimum api level of the current device.
 * @param minApiLevel The api level to verify
 * @return True if the device is loaded with the minimum API level specified
 * or higher.
 */
fun hasMinimumApi(minApiLevel: Int): Boolean {
    return Build.VERSION.SDK_INT >= minApiLevel
}
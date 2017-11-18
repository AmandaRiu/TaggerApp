package com.amandariu.tagger.demo.utils;

import android.os.Build;

/**
 * Misc android related utilities
 *
 * @author amandariu (11/5/17)
 */
public abstract class AndroidUtils {
    /**
     * Verifies the minimum api level of the current device.
     * @param minApiLevel The api level to verify
     * @return True if the device is loaded with the minimum API level specified
     * or higher.
     */
    public static boolean hasMinimumApi(int minApiLevel) {
        return Build.VERSION.SDK_INT >= minApiLevel;
    }
}

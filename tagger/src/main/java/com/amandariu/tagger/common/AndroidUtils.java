package com.amandariu.tagger.common;

import android.content.Context;

/**
 * Helper for various android-related utilities.
 *
 * @author amandariu (11/18/17)
 */
public abstract class AndroidUtils {

    public static int getDensityPixel(Context context, int dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
}

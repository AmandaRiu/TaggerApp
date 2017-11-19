package com.amandariu.tagger;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;

/**
 * @author amandariu (11/18/17)
 */
public final class TagUtils {
    private TagUtils() throws InstantiationException {
        throw new InstantiationException("Utility class only!");
    }

    public static int getDensityPixel(Context context, int dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static int getColorInt(@NonNull String colorString) {
        //
        // Try prepending it with a '#' symbol. Would've checked for this
        // first but it actually covers two different scenarios if we just attempt
        // it first and then do this.
        String newColorString = "#" + colorString;
        int result = Color.parseColor(newColorString);
        if (result == -1) {
            return 0xFF666666;
        }
        return result;
    }
}

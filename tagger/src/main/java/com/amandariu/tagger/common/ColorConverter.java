package com.amandariu.tagger.common;

import android.graphics.Color;
import android.support.annotation.NonNull;

/**
 * @author amandariu (11/12/17)
 */
public abstract class ColorConverter {

    public static int getColorInt(@NonNull String colorString) {
        int result = Color.parseColor(colorString);
        if (result == -1) {
            //
            // Try prepending it with a '#' symbol. Would've checked for this
            // first but it actually covers two different scenarios if we just attempt
            // it first and then do this.
            result = Color.parseColor("#" + colorString);
        }

        return result;
    }
}

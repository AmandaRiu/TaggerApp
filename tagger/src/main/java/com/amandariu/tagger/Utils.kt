package com.amandariu.tagger

import android.content.Context
import android.graphics.Color

/**
 * Converts a pixel to a density pixel to match the density of
 * the device.
 */
fun getDensityPixel(context: Context, dps: Int): Int {
    val scale = context.resources.displayMetrics.density
    return (dps * scale + 0.5f).toInt()
}

/**
 * Converts a string (example: ff335544) into a color int. First it will at
 */
fun getColorInt(colorString: String): Int {
    val result: Int
    if (!colorString.startsWith("#")) {
        result = Color.parseColor("#" + colorString)
    } else {
        result = Color.parseColor(colorString)
    }

    return if (result == -1) {
        -0x99999a
    } else result
}

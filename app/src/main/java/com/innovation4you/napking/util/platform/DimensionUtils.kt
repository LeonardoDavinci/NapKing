package com.innovation4you.napking.util.platform

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue

object DimensionUtils {

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.

     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * *
     * @param context Context to get resources and device specific display metrics
     * *
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDipToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return px
    }

    /**
     * This method converts device specific pixels to density independent pixels.

     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * *
     * @param context Context to get resources and device specific display metrics
     * *
     * @return A float value to represent dp equivalent to px value
     */
    fun convertPixelsToDip(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val dp = px / (metrics.densityDpi / 160f)
        return dp
    }

    fun convertSpToPixels(sp: Float, context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    }
}

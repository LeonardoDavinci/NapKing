package com.innovation4you.napking.util.platform

import android.os.Build

object APILevelUtils {

    /*
	 * Checks if the current API version is higher or equal to the required level
	 */
    fun isHigherOrEqual(level: Int): Boolean {
        return Build.VERSION.SDK_INT >= level
    }

    fun supportStatusBarColor(): Boolean {
        return isHigherOrEqual(21)
    }

    fun supportsElevation(): Boolean {
        return isHigherOrEqual(21)
    }

    fun supportTransitions(): Boolean {
        return isHigherOrEqual(19)
    }

    fun supportCircularReveal(): Boolean {
        return isHigherOrEqual(21)
    }
}

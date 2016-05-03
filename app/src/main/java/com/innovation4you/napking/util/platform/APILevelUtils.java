package com.innovation4you.napking.util.platform;

import android.os.Build;

public class APILevelUtils {

	/*
	 * Checks if the current API version is higher or equal to the required level
	 */
	public static boolean isHigherOrEqual(int level) {
		return Build.VERSION.SDK_INT >= level;
	}

	public static boolean supportStatusBarColor() {
		return isHigherOrEqual(21);
	}

	public static boolean supportsElevation() {
		return isHigherOrEqual(21);
	}

	public static boolean supportTransitions() {
		return isHigherOrEqual(19);
	}

	public static boolean supportCircularReveal() {
		return isHigherOrEqual(21);
	}
}

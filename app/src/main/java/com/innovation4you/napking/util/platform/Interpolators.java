package com.innovation4you.napking.util.platform;

import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

public class Interpolators {

	private static LinearOutSlowInInterpolator linearOutSlowInInterpolator = new LinearOutSlowInInterpolator();
	private static AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
	private static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();

	public static LinearOutSlowInInterpolator getLinearOutSlowInInterpolator() {
		return linearOutSlowInInterpolator;
	}

	public static AccelerateDecelerateInterpolator getAccelerateDecelerateInterpolator() {
		return accelerateDecelerateInterpolator;
	}

	public static OvershootInterpolator getOvershootInterpolator() {
		return overshootInterpolator;
	}
}

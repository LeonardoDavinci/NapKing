package com.innovation4you.napking.util.platform

import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator

object Interpolators {

    val linearOutSlowInInterpolator = LinearOutSlowInInterpolator()
    val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()
    val overshootInterpolator = OvershootInterpolator()
}

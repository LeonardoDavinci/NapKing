package com.innovation4you.napking.util.platform

import android.view.View

abstract class OneTimeLayoutChangeListener : View.OnLayoutChangeListener {

    override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        v.removeOnLayoutChangeListener(this)
        onLayoutChange()
    }

    abstract fun onLayoutChange()
}

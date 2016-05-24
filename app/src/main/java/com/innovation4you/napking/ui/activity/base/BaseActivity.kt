package com.innovation4you.napking.ui.activity.base

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

import com.innovation4you.napking.ui.fragment.BaseFragment

import org.greenrobot.eventbus.EventBus

import butterknife.ButterKnife
import butterknife.Unbinder

abstract class BaseActivity : AppCompatActivity() {

    private var unbinder: Unbinder? = null

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        unbinder = ButterKnife.bind(this)
    }

    public override fun onStart() {
        super.onStart()

        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onBackPressed() {
        val handled = dispatchBackPressToFragments()
        if (!handled) {
            super.onBackPressed()
        }
    }

    protected fun dispatchBackPressToFragments(): Boolean {
        var handled = false
        try {
            for (f in supportFragmentManager.fragments) {
                if (f is BaseFragment) {
                    handled = handled or f.onBackPressed()
                }
            }
        } catch (e: Exception) {
        }

        return handled
    }

    public override fun onStop() {
        super.onStop()

        if (useEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    protected open fun useEventBus(): Boolean {
        return false
    }

    fun checkFragmentsOnActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        for (f in supportFragmentManager.fragments) {
            f?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (unbinder != null) {
            unbinder!!.unbind()
        }
    }
}

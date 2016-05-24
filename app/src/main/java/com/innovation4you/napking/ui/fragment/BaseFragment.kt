package com.innovation4you.napking.ui.fragment

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.innovation4you.napking.util.platform.OneTimeLayoutChangeListener
import org.greenrobot.eventbus.EventBus

open class BaseFragment : Fragment() {

    protected var root: View? = null
    protected var isViewCreated: Boolean = false
        private set
    private var unbinder: Unbinder? = null

    protected fun setContentView(@LayoutRes layoutResId: Int, inflater: LayoutInflater?, container: ViewGroup?
                                 , savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        root = inflater?.inflate(layoutResId, container, false)
        root?.addOnLayoutChangeListener(object : OneTimeLayoutChangeListener() {
            override fun onLayoutChange() {
                onLayoutFinished()
            }
        })
        unbinder = ButterKnife.bind(this, root as View)
        isViewCreated = true
        return root
    }

    protected open fun onLayoutFinished() {
    }

    override fun onStart() {
        super.onStart()
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (useEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        root = null
        if (unbinder != null) {
            unbinder!!.unbind()
        }
    }

    protected fun useEventBus(): Boolean {
        return false
    }

    fun onBackPressed(): Boolean {
        return false
    }

}

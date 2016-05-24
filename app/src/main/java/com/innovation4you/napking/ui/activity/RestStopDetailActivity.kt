package com.innovation4you.napking.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle

import com.innovation4you.napking.model.SearchResult
import com.innovation4you.napking.ui.activity.base.ContainerActivity
import com.innovation4you.napking.ui.fragment.RestStopDetailFragment

class RestStopDetailActivity : ContainerActivity<RestStopDetailFragment>() {

    override fun createFragment(args: Bundle): RestStopDetailFragment {
        return RestStopDetailFragment.newInstance(args)
    }

    companion object {

        fun createIntent(context: Context, searchResult: SearchResult): Intent {
            val i = Intent(context, RestStopDetailActivity::class.java)
            i.putExtra(RestStopDetailFragment.KEY_SEARCH_RESULT, searchResult)
            return i
        }
    }
}

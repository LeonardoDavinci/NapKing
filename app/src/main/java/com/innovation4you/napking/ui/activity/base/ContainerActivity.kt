package com.innovation4you.napking.ui.activity.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import android.widget.FrameLayout
import butterknife.BindView
import com.innovation4you.napking.R

abstract class ContainerActivity<T : Fragment> : BaseActivity() {

    @BindView(R.id.activity_fragment_container_content)
    var container: FrameLayout? = null

    protected lateinit var fragment: T

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fragment_container)

        if (savedInstanceState == null) {
            fragment = createFragment(intent.extras)
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.activity_fragment_container_content, fragment)
            ft.commit()
        } else {
            fragment = supportFragmentManager.findFragmentById(R.id.activity_fragment_container_content) as T
        }
    }

    protected abstract fun createFragment(args: Bundle): T

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
    }
}

package com.innovation4you.napking.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.innovation4you.napking.ui.fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

	private Unbinder unbinder;

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		unbinder = ButterKnife.bind(this);
	}

	@Override
	public void onStart() {
		super.onStart();

		if (useEventBus()) {
			EventBus.getDefault().register(this);
		}
	}

	@Override
	public void onBackPressed() {
		boolean handled = dispatchBackPressToFragments();
		if (!handled) {
			super.onBackPressed();
		}
	}

	protected boolean dispatchBackPressToFragments() {
		boolean handled = false;
		try {
			for (Fragment f : getSupportFragmentManager().getFragments()) {
				if (f instanceof BaseFragment) {
					handled |= ((BaseFragment) f).onBackPressed();
				}
			}
		} catch (Exception e) {
		}
		return handled;
	}

	@Override
	public void onStop() {
		super.onStop();

		if (useEventBus() && EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}

	protected boolean useEventBus() {
		return false;
	}

	public void checkFragmentsOnActivityResult(int requestCode, int resultCode, Intent data) {
		for (Fragment f : getSupportFragmentManager().getFragments()) {
			if (f != null) {
				f.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (unbinder != null) {
			unbinder.unbind();
		}
	}
}

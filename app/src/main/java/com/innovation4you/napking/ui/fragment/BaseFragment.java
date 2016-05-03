package com.innovation4you.napking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.innovation4you.napking.util.platform.OneTimeLayoutChangeListener;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseFragment extends Fragment {

	protected View root;
	private boolean isViewCreated;
	private Unbinder unbinder;

	protected View setContentView(@LayoutRes final int layoutResId, final LayoutInflater inflater, final ViewGroup container, final Bundle
			savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		root = inflater.inflate(layoutResId, container, false);
		root.addOnLayoutChangeListener(new OneTimeLayoutChangeListener() {
			@Override
			public void onLayoutChange() {
				onLayoutFinished();
			}
		});
		unbinder = ButterKnife.bind(this, root);
		isViewCreated = true;
		return root;
	}

	protected void onLayoutFinished() {
	}

	@Override
	public void onStart() {
		super.onStart();
		if (useEventBus()) {
			EventBus.getDefault().register(this);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (useEventBus() && EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		isViewCreated = false;
		root = null;
		if (unbinder != null) {
			unbinder.unbind();
		}
	}

	protected boolean useEventBus() {
		return false;
	}

	protected boolean isViewCreated() {
		return isViewCreated;
	}

	public boolean onBackPressed() {
		return false;
	}

}

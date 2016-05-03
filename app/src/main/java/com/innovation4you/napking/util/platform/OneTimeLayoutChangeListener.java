package com.innovation4you.napking.util.platform;

import android.view.View;

public abstract class OneTimeLayoutChangeListener implements View.OnLayoutChangeListener {

	@Override
	public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		v.removeOnLayoutChangeListener(this);
		onLayoutChange();
	}

	public abstract void onLayoutChange();
}

package com.amerbauer.seachbox.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class BackKeyEditText extends EditText {

	public OnKeyListener keyListener;

	public void setBackKeyListener(OnKeyListener keyListener) {
		this.keyListener = keyListener;
	}

	public BackKeyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (keyListener != null) {
			keyListener.onKey(this, keyCode, event);
		}
		return false;
	}
}
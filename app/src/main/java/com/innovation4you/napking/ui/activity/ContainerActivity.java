package com.innovation4you.napking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.innovation4you.napking.R;

import butterknife.BindView;

public abstract class ContainerActivity<T extends Fragment> extends BaseActivity {

	@BindView(R.id.activity_fragment_container_content)
	protected FrameLayout container;

	protected T fragment;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fragment_container);

		if (savedInstanceState == null) {
			fragment = createFragment(getIntent().getExtras());
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.activity_fragment_container_content, fragment);
			ft.commit();
		} else {
			fragment = (T) getSupportFragmentManager().findFragmentById(R.id.activity_fragment_container_content);
		}
	}

	protected abstract T createFragment(final Bundle args);

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}

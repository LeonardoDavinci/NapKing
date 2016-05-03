package com.innovation4you.napking.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.amerbauer.seachbox.view.SearchBox;
import com.innovation4you.napking.R;
import com.innovation4you.napking.ui.fragment.RestStopDetailFragment;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

	@BindView(R.id.activity_main_searchbox)
	SearchBox searchBox;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.activity_main_list_container, new RestStopDetailFragment());
			ft.commit();
		}
	}
}

package com.innovation4you.napking.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.amerbauer.seachbox.view.SearchBox;
import com.innovation4you.napking.R;
import com.innovation4you.napking.data.NapKingService;
import com.innovation4you.napking.model.RestStop;
import com.innovation4you.napking.ui.activity.base.BaseActivity;
import com.innovation4you.napking.ui.fragment.RestStopListFragment;

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
			ft.add(R.id.activity_main_list_container, new RestStopListFragment());
			ft.commit();
		}
		
		showRestStop(NapKingService.getRestStop(0));
	}

	private void showRestStop(final RestStop restStop) {
		startActivity(RestStopDetailActivity.createIntent(this, restStop.id));
	}
}

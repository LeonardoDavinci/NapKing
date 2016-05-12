package com.innovation4you.napking.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.amerbauer.seachbox.view.SearchBox;
import com.innovation4you.napking.R;
import com.innovation4you.napking.data.NapKingRepository;
import com.innovation4you.napking.model.SearchResult;
import com.innovation4you.napking.ui.activity.base.BaseActivity;
import com.innovation4you.napking.ui.fragment.RestStopListFragment;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<List<SearchResult>> {

	private static final String KEY_SEARCH_TEXT = "text";
	private static final String KEY_SEARCH_MINUTES_LEFT = "minutes_left";

	@BindView(R.id.activity_main_searchbox)
	SearchBox searchBox;
	private RestStopListFragment restStopListFragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			restStopListFragment = new RestStopListFragment();
			ft.add(R.id.activity_main_list_container, restStopListFragment);
			ft.commit();
		} else {
			restStopListFragment = (RestStopListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_list_container);
		}

		search("", 300);
	}

	private void search(final String text, final int minutesLeft) {
		final Bundle args = new Bundle();
		args.putString(KEY_SEARCH_TEXT, text);
		args.putInt(KEY_SEARCH_MINUTES_LEFT, minutesLeft);
		getSupportLoaderManager().initLoader(1, args, this).forceLoad();
	}

	@Override
	public Loader<List<SearchResult>> onCreateLoader(int id, Bundle args) {
		final String text = args.getString(KEY_SEARCH_TEXT);
		final int minutesLeft = args.getInt(KEY_SEARCH_MINUTES_LEFT);

		return new AsyncTaskLoader<List<SearchResult>>(this) {
			@Override
			public List<SearchResult> loadInBackground() {
				return NapKingRepository.get().search(text, minutesLeft, 48.2951579, 14.2573656);
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<List<SearchResult>> loader, List<SearchResult> data) {
		if (restStopListFragment != null) {
			restStopListFragment.setSearchResults(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<SearchResult>> loader) {
	}
}

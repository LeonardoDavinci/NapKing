package com.innovation4you.napking.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

import com.amerbauer.seachbox.view.SearchBox;
import com.binaryfork.spanny.Spanny;
import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;
import com.innovation4you.napking.R;
import com.innovation4you.napking.data.NapKingRepository;
import com.innovation4you.napking.model.RestStop;
import com.innovation4you.napking.model.SearchResult;
import com.innovation4you.napking.ui.activity.base.BaseActivity;
import com.innovation4you.napking.ui.fragment.RestStopListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<List<SearchResult>>, HmsPickerDialogFragment
		.HmsPickerDialogHandler {

	private static final String KEY_SEARCH_TEXT = "text";
	private static final String KEY_SEARCH_MINUTES_LEFT = "minutes_left";

	@BindView(R.id.activity_main_searchbox)
	SearchBox searchBox;

	@BindView(R.id.activity_main_button_driving_time)
	TextView btDrivingTime;

	RestStopListFragment restStopListFragment;

	boolean isResultsListShown;
	int drivingMinutesLeft = 90;

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

		isResultsListShown = true;

		updateDrivingTimeLeftButton();

		//getSupportLoaderManager().initLoader(1, null, this);
		setupSearchBox();
		updateSearchBox();

		loadSearchResults("", 300);
	}

	@OnClick(R.id.activity_main_button_driving_time)
	public void onDrivingTimeClick() {
		final HmsPickerBuilder hpb = new HmsPickerBuilder()
				.setTimeInSeconds(drivingMinutesLeft * 60)
				.addHmsPickerDialogHandler(this)
				.setFragmentManager(getSupportFragmentManager())
				.setStyleResId(R.style.BetterPickersDialogFragment);
		hpb.show();
	}

	private void setupSearchBox() {
		searchBox.setHint("Enter rest stop or location");
		searchBox.setSearchListener(new SearchBox.SearchListener() {
			@Override
			public void onSearchOpened() {
			}

			@Override
			public void onSearchCleared() {
				search(null);
			}

			@Override
			public void onSearchClosed() {
			}

			@Override
			public void onSearchTermChanged(String term) {
				updateSuggestions(term);
			}

			@Override
			public void onSearch(String result) {
				search(result);
			}

			@Override
			public void onSuggestionClick(SearchBox.SuggestionItem result) {
				search(((RestStop) result.tag).name);
			}
		});
	}

	private void updateSuggestions(final String term) {
		final ArrayList<SearchBox.SuggestionItem> suggestions = new ArrayList<>();
		final Drawable icon = getResources().getDrawable(R.drawable.ic_parking_black_24dp);
		for (RestStop restStop : NapKingRepository.get().findRestStops(term)) {
			suggestions.add(new SearchBox.SuggestionItem(restStop.name, icon, restStop));
		}

		if (suggestions.isEmpty()) {
			searchBox.hideSuggestions();
		} else {
			searchBox.setSuggestionItems(suggestions);
		}
	}

	private void updateSearchBox() {
		searchBox.setBackButtonVisible(isResultsListShown);
	}

	private void search(final String text) {
		loadSearchResults(text, drivingMinutesLeft);
	}

	private void loadSearchResults(final String text, final int minutesLeft) {
		if (text == null) {
			if (restStopListFragment != null) {
				restStopListFragment.setSearchResults(null);
			}
		} else {
			final Bundle args = new Bundle();
			args.putString(KEY_SEARCH_TEXT, text);
			args.putInt(KEY_SEARCH_MINUTES_LEFT, minutesLeft);
			getSupportLoaderManager().restartLoader(1, args, this).forceLoad();
		}
	}

	@Override
	public Loader<List<SearchResult>> onCreateLoader(int id, Bundle args) {
		searchBox.setLoading(true);
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
		searchBox.setLoading(false);
	}

	@Override
	public void onLoaderReset(Loader<List<SearchResult>> loader) {
	}

	@Override
	public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
		drivingMinutesLeft = hours * 60 + minutes;
		updateDrivingTimeLeftButton();
		search(searchBox.getSearchTerm());
	}

	private void updateDrivingTimeLeftButton() {
		final Spanny span = new Spanny();
		final int hours = drivingMinutesLeft / 60;
		final int minutes = drivingMinutesLeft % 60;

		if (hours > 0) {
			span.append(String.valueOf(hours));
			span.append(" h", new TypefaceSpan("sans-serif-light"));
			if (minutes > 0) {
				span.append(" ");
			}
		}

		if (minutes > 0) {
			span.append(String.valueOf(minutes));
			span.append(" m", new TypefaceSpan("sans-serif-light"));
		}

		span.append(" driving time left", new TypefaceSpan("sans-serif-light"));

		btDrivingTime.setText(span);
	}
}

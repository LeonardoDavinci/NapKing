package com.innovation4you.napking.ui.activity;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amerbauer.seachbox.view.SearchBox;
import com.binaryfork.spanny.Spanny;
import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.innovation4you.napking.R;
import com.innovation4you.napking.app.Cfg;
import com.innovation4you.napking.data.NapKingRepository;
import com.innovation4you.napking.model.RestStop;
import com.innovation4you.napking.model.SearchResult;
import com.innovation4you.napking.ui.activity.base.BaseActivity;
import com.innovation4you.napking.ui.fragment.RestStopListFragment;
import com.innovation4you.napking.util.MessageUtils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<List<SearchResult>>, HmsPickerDialogFragment
		.HmsPickerDialogHandler, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback
		<PlaceBuffer> {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String KEY_SEARCH_DESTINATION = "destination";
	private static final String KEY_SEARCH_SOURCE = "source";
	private static final String KEY_SEARCH_MINUTES_LEFT = "minutes_left";
	private static final int REQUEST_CODE_LOCATION = 202;

	@BindView(R.id.activity_main_searchbox)
	SearchBox searchBox;

	@BindView(R.id.activity_main_button_driving_time)
	TextView btDrivingTime;

	@BindView(R.id.layout_current_location_progress)
	ProgressBar pbLocation;

	@BindView(R.id.layout_current_location_text)
	TextView tvLocation;

	@BindView(R.id.layout_current_location_container)
	ViewGroup locationContainer;

	RestStopListFragment restStopListFragment;

	boolean isResultsListShown;
	int drivingMinutesLeft = 120;

	GoogleApiClient googleApiClient;
	LatLng currentDestination;
	LatLng currentLocation;

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

		setupSearchBox();
		updateSearchBox();

		updateLocationContainer();

		googleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
	}

	private void updateLocationContainer() {
		if (locationContainer.getVisibility() != View.VISIBLE) {
			locationContainer.setTranslationY(100f);
			locationContainer.setAlpha(0f);
			locationContainer.setVisibility(View.VISIBLE);
			locationContainer.animate().alpha(1f).translationY(0f).setStartDelay(500).setDuration(700)
					.setInterpolator(new FastOutSlowInInterpolator());
		}
		if (currentLocation != null) {
			pbLocation.setVisibility(View.INVISIBLE);
			tvLocation.setText(getString(R.string.current_location, currentLocation.toString()));
		} else {
			pbLocation.setVisibility(View.VISIBLE);
			tvLocation.setText(R.string.waiting_for_location);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		googleApiClient.connect();
	}

	@Override
	public void onStop() {
		googleApiClient.disconnect();
		super.onStop();
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
				clearSearchResults();
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
			}

			@Override
			public void onSuggestionClick(SearchBox.SuggestionItem result) {
				if (result.tag instanceof RestStop) {
					// Show rest stop detail
				} else if (result.tag instanceof AutocompletePrediction) {
					searchBox.setLoading(true);
					PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
							.getPlaceById(googleApiClient, ((AutocompletePrediction) result.tag).getPlaceId());
					placeResult.setResultCallback(MainActivity.this);
				}
			}
		});
	}

	private void updateSuggestions(String term) {
		new AsyncTask<String, Void, List<SearchBox.SuggestionItem>>() {
			@Override
			protected void onPreExecute() {
				searchBox.setLoading(true);
			}

			@Override
			protected List<SearchBox.SuggestionItem> doInBackground(String... params) {
				final String search = params[0];
				final ArrayList<SearchBox.SuggestionItem> suggestions = new ArrayList<>();

				final Drawable restStopIcon = getResources().getDrawable(R.drawable.ic_parking_black_24dp);
				for (RestStop restStop : NapKingRepository.get().findRestStops(search)) {
					suggestions.add(new SearchBox.SuggestionItem(restStop.name, restStopIcon, restStop));
				}

				final ArrayList<AutocompletePrediction> places = getGooglePlacesAutocomplete(search);
				if (places != null) {
					for (AutocompletePrediction prediction : places) {
						suggestions.add(new SearchBox.SuggestionItem(prediction.getPrimaryText(null).toString(), restStopIcon,
								prediction));
					}
				}

				return suggestions;
			}

			@Override
			protected void onPostExecute(List<SearchBox.SuggestionItem> suggestions) {
				searchBox.setLoading(false);
				if (suggestions.isEmpty()) {
					searchBox.hideSuggestions();
				} else {
					searchBox.setSuggestionItems(suggestions);
				}
			}
		}.execute(term);
	}

	private void updateSearchBox() {
		searchBox.setBackButtonVisible(isResultsListShown);
	}

	private void clearSearchResults() {
		if (restStopListFragment != null) {
			restStopListFragment.setSearchResults(null);
		}
	}

	private void loadSearchResults() {
		if (currentLocation == null) {
			MessageUtils.showErrorMessageWithRetry(this, R.string.waiting_for_location, new ActionClickListener() {
				@Override
				public void onActionClicked(Snackbar snackbar) {
					loadSearchResults();
				}
			});
			return;
		}

		final Bundle args = new Bundle();
		args.putParcelable(KEY_SEARCH_DESTINATION, currentDestination);
		args.putParcelable(KEY_SEARCH_SOURCE, Cfg.USE_FAKE_LOCATION ? new LatLng(48.2951579, 14.2573656) : currentLocation);
		args.putInt(KEY_SEARCH_MINUTES_LEFT, drivingMinutesLeft);
		getSupportLoaderManager().restartLoader(1, args, this).forceLoad();
	}

	@Override
	public Loader<List<SearchResult>> onCreateLoader(int id, Bundle args) {
		searchBox.setLoading(true);

		final LatLng destination = args.getParcelable(KEY_SEARCH_DESTINATION);
		final LatLng source = args.getParcelable(KEY_SEARCH_SOURCE);
		final int minutesLeft = args.getInt(KEY_SEARCH_MINUTES_LEFT);

		return new AsyncTaskLoader<List<SearchResult>>(this) {
			@Override
			public List<SearchResult> loadInBackground() {
				return NapKingRepository.get().search(source, destination, minutesLeft);
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
		loadSearchResults();
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

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		getCurrentLocation();
	}

	private void getCurrentLocation() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
			return;
		}
		final Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
		currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
		updateLocationContainer();
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, 112);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
		}
	}

	private ArrayList<AutocompletePrediction> getGooglePlacesAutocomplete(final CharSequence constraint) {
		if (googleApiClient.isConnected()) {
			Log.i(TAG, "Starting autocomplete query for: " + constraint);

			// Submit the query to the autocomplete API and retrieve a PendingResult that will
			// contain the results when the query completes.
			PendingResult<AutocompletePredictionBuffer> results =
					Places.GeoDataApi.getAutocompletePredictions(googleApiClient, constraint.toString(), null, null);
			//new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).build());

			// This method should have been called off the main UI thread. Block and wait for at most 60s
			// for a result from the API.
			AutocompletePredictionBuffer autocompletePredictions = results
					.await(60, TimeUnit.SECONDS);

			// Confirm that the query completed successfully, otherwise return null
			final Status status = autocompletePredictions.getStatus();
			if (!status.isSuccess()) {
				Toast.makeText(this, "Error contacting API: " + status.toString(),
						Toast.LENGTH_SHORT).show();
				Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
				autocompletePredictions.release();
				return null;
			}

			Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
					+ " predictions.");

			// Freeze the results immutable representation that can be stored safely.
			return DataBufferUtils.freezeAndClose(autocompletePredictions);
		}
		Log.e(TAG, "Google API client is not connected for autocomplete query.");
		return null;
	}

	@Override
	public void onResult(@NonNull PlaceBuffer places) {
		if (!places.getStatus().isSuccess()) {
			// Request did not complete successfully
			Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
			places.release();
			return;
		}
		// Get the Place object from the buffer.
		final Place place = places.get(0);
		Log.i(TAG, "Place details received: " + place.getName());
		currentDestination = place.getLatLng();
		loadSearchResults();

		places.release();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE_LOCATION) {
			getCurrentLocation();
		}
	}
}

package com.innovation4you.napking.ui.activity

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.OnClick
import com.amerbauer.seachbox.view.SearchBox
import com.binaryfork.spanny.Spanny
import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.innovation4you.napking.R
import com.innovation4you.napking.app.Cfg
import com.innovation4you.napking.data.NapKingRepository
import com.innovation4you.napking.event.ActivityDetectedEvent
import com.innovation4you.napking.model.RestStop
import com.innovation4you.napking.model.SearchResult
import com.innovation4you.napking.service.ActivityRecognitionIntentService
import com.innovation4you.napking.ui.activity.base.BaseActivity
import com.innovation4you.napking.ui.fragment.RestStopListFragment
import com.innovation4you.napking.util.CheatSheet
import com.innovation4you.napking.util.MessageUtils
import com.innovation4you.napking.util.platform.Interpolators
import com.innovation4you.napking.util.platform.OneTimeLayoutChangeListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity(), LoaderManager.LoaderCallbacks<List<SearchResult>>,
        HmsPickerDialogFragment.HmsPickerDialogHandler, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<PlaceBuffer> {

    @BindView(R.id.activity_main_container)
    private lateinit var rootContainer: ViewGroup

    @BindView(R.id.activity_main_searchbox)
    private lateinit var searchBox: SearchBox

    @BindView(R.id.activity_main_search_background)
    private lateinit var vSearchBackground: View

    @BindView(R.id.activity_main_empty_text)
    private lateinit var tvEmptyResults: TextView

    @BindView(R.id.activity_main_button_driving_time)
    private lateinit var btDrivingTime: TextView

    @BindView(R.id.activity_main_driving_indicator)
    private lateinit var ivDrivingIndicator: ImageView

    @BindView(R.id.layout_current_location_image)
    private lateinit var ivCurrentLocation: ImageView

    @BindView(R.id.layout_current_location_backround)
    private lateinit var vLocationBackground: View

    @BindView(R.id.layout_current_location_progress)
    private lateinit var pbLocation: ProgressBar

    @BindView(R.id.layout_current_location_text)
    private lateinit var tvLocation: TextView

    @BindView(R.id.layout_current_location_container)
    private lateinit var vgLocationContainer: ViewGroup

    private lateinit var restStopListFragment: RestStopListFragment

    private var isInResultsMode: Boolean = false
    private var drivingMinutesLeft = 120

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var currentDestination: LatLng
    private var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val ft = supportFragmentManager.beginTransaction()
            restStopListFragment = RestStopListFragment()
            ft.add(R.id.activity_main_list_container, restStopListFragment)
            ft.commit()
        } else {
            restStopListFragment = supportFragmentManager.findFragmentById(R.id.activity_main_list_container) as RestStopListFragment
        }

        rootContainer.addOnLayoutChangeListener(object : OneTimeLayoutChangeListener() {
            override fun onLayoutChange() {
                rootContainer.removeOnLayoutChangeListener(this)
                changeMode(false, false)
            }
        })

        updateDrivingTimeLeftButton()

        setupSearchBox()

        updateLocationContainer()

        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()

        vgLocationContainer.setOnClickListener { changeMode(!isInResultsMode, true) }

        CheatSheet.setup(ivDrivingIndicator, "Detected driving - reducing permissible driving time")
    }

    override fun onStart() {
        super.onStart()
        googleApiClient.connect()
    }

    override fun onStop() {
        if (googleApiClient.isConnected) {
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient,
                    createActivityRecognitionIntentServicePendingIntent())
            Log.d(TAG, "Removed activity detection")
        }
        googleApiClient.disconnect()
        super.onStop()
    }

    @OnClick(R.id.activity_main_button_driving_time)
    fun onDrivingTimeClick() {
        val hpb = HmsPickerBuilder().setTimeInSeconds(drivingMinutesLeft * 60).addHmsPickerDialogHandler(this).setFragmentManager(supportFragmentManager).setStyleResId(R.style.BetterPickersDialogFragment)
        hpb.show()
    }

    private fun setupSearchBox() {
        searchBox!!.setHint("Enter rest stop or location")
        searchBox!!.setOnBackButtonClickListener { onBackPressed() }
        searchBox!!.setSearchListener(object : SearchBox.SearchListener {
            override fun onSearchOpened() {
            }

            override fun onSearchCleared() {
                clearSearchResults()
            }

            override fun onSearchClosed() {
            }

            override fun onSearchTermChanged(term: String) {
                updateSuggestions(term)
            }

            override fun onSearch(result: String) {
            }

            override fun onSuggestionClick(result: SearchBox.SuggestionItem) {
                if (result.tag is RestStop) {
                    // Show rest stop detail
                } else if (result.tag is AutocompletePrediction) {
                    searchBox!!.setLoading(true)
                    val placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, (result.tag as AutocompletePrediction).placeId)
                    placeResult.setResultCallback(this@MainActivity)
                }
            }
        })
    }

    private fun updateSuggestions(term: String) {
        object : AsyncTask<String, Void, List<SearchBox.SuggestionItem>>() {
            override fun onPreExecute() {
                searchBox!!.setLoading(true)
            }

            override fun doInBackground(vararg params: String): List<SearchBox.SuggestionItem> {
                val search = params[0]
                val suggestions = ArrayList<SearchBox.SuggestionItem>()
                val restStopIcon = resources.getDrawable(R.drawable.ic_parking_black_24dp)
                val placeIcon = resources.getDrawable(R.drawable.ic_map_marker_black_36dp)

                for (restStop in NapKingRepository.get().findRestStops(search)) {
                    suggestions.add(SearchBox.SuggestionItem(restStop.name, restStopIcon, restStop))
                }

                val places = getGooglePlacesAutocomplete(search)
                if (places != null) {
                    for (prediction in places) {
                        suggestions.add(SearchBox.SuggestionItem(prediction.getPrimaryText(null).toString(), placeIcon,
                                prediction))
                    }
                }

                return suggestions
            }

            override fun onPostExecute(suggestions: List<SearchBox.SuggestionItem>) {
                searchBox!!.setLoading(false)
                if (suggestions.isEmpty()) {
                    searchBox!!.hideSuggestions()
                } else {
                    searchBox!!.setSuggestionItems(suggestions)
                }
            }
        }.execute(term)
    }

    private fun updateSearchBox() {
        searchBox!!.setBackButtonVisible(isInResultsMode)
        searchBox!!.isSelected = false
    }

    private fun updateLocationContainer() {
        if (vgLocationContainer!!.visibility != View.VISIBLE) {
            vgLocationContainer!!.translationY = 100f
            vgLocationContainer!!.alpha = 0f
            vgLocationContainer!!.visibility = View.VISIBLE
            vgLocationContainer!!.animate().alpha(1f).translationY(0f).setStartDelay(500).setDuration(700).interpolator = FastOutSlowInInterpolator()
        }

        if (currentLocation != null) {
            pbLocation!!.visibility = View.INVISIBLE
            ivCurrentLocation!!.visibility = View.VISIBLE
            val span = Spanny(getString(R.string.current_location) + " ", TypefaceSpan("sans-serif-light")).append(currentLocation!!.latitude.toString()).append(", ").append(currentLocation!!.longitude.toString())
            tvLocation!!.text = span
        } else {
            pbLocation!!.visibility = View.VISIBLE
            ivCurrentLocation!!.visibility = View.INVISIBLE
            tvLocation!!.setText(R.string.waiting_for_location)
        }
    }

    private fun clearSearchResults() {
        if (restStopListFragment != null) {
            restStopListFragment!!.setSearchResults(null)
        }
    }


    private fun loadSearchResults() {
        if (currentLocation == null) {
            MessageUtils.showErrorMessage(this, R.string.waiting_for_location)
            return
        }

        val args = Bundle()
        args.putParcelable(KEY_SEARCH_DESTINATION, currentDestination)
        args.putParcelable(KEY_SEARCH_SOURCE, if (Cfg.USE_FAKE_LOCATION) LatLng(48.2951579, 14.2573656) else currentLocation)
        args.putInt(KEY_SEARCH_MINUTES_LEFT, drivingMinutesLeft)
        supportLoaderManager.restartLoader(1, args, this).forceLoad()
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<List<SearchResult>> {
        searchBox.setLoading(true)

        val destination = args.getParcelable<LatLng>(KEY_SEARCH_DESTINATION)
        val source = args.getParcelable<LatLng>(KEY_SEARCH_SOURCE)
        val minutesLeft = args.getInt(KEY_SEARCH_MINUTES_LEFT)

        return object : AsyncTaskLoader<List<SearchResult>>(this) {
            override fun loadInBackground(): List<SearchResult> {
                return NapKingRepository.get().search(source, destination, minutesLeft)
            }
        }
    }

    override fun onLoadFinished(loader: Loader<List<SearchResult>>, data: List<SearchResult>?) {
        searchBox!!.setLoading(false)
        val hasData = data != null && !data.isEmpty()

        if (restStopListFragment != null) {
            restStopListFragment!!.setSearchResults(data)
            restStopListFragment!!.view!!.animate().alpha(if (hasData) 1f else 0f)
        }
        tvEmptyResults!!.animate().alpha(if (hasData) 0f else 1f)
        changeMode(true, true)
    }

    override fun onLoaderReset(loader: Loader<List<SearchResult>>) {
    }

    override fun onBackPressed() {
        if (isInResultsMode) {
            changeMode(false, true)
        } else {
            super.onBackPressed()
        }
    }

    private fun changeMode(showResultsMode: Boolean, animate: Boolean) {
        isInResultsMode = showResultsMode

        var backgroundScale = 1f
        var searchBoxTranslationY = 0f
        var drivingTimeButtonScale = 1f
        var drivingTimeButtonTranslationY = 0f
        var resultsListAlpha = 1f
        var resultsListTranslationY = 0f
        var locationBackgroundAlpha = 1f
        var locationContainerTranslationY = 0f
        var locationContainerScale = 1f

        if (!showResultsMode) {
            val rootContainerHeight = rootContainer!!.height.toFloat()
            backgroundScale = rootContainerHeight / vSearchBackground!!.height
            searchBoxTranslationY = rootContainerHeight / 4 - searchBox!!.y
            drivingTimeButtonTranslationY = rootContainerHeight / 4 - searchBox!!.height.toFloat() - btDrivingTime!!.height * 2.2f
            drivingTimeButtonScale = 1.5f
            resultsListAlpha = 0f
            resultsListTranslationY = 300f
            locationBackgroundAlpha = 0f
            locationContainerTranslationY = -300f
            locationContainerScale = 0.8f
        }

        vSearchBackground!!.animate().scaleY(backgroundScale).setInterpolator(Interpolators.linearOutSlowInInterpolator).duration = (if (animate) 700 else 0).toLong()
        searchBox!!.animate().translationY(searchBoxTranslationY).setInterpolator(Interpolators.linearOutSlowInInterpolator).duration = (if (animate) 700 else 0).toLong()
        btDrivingTime!!.animate().scaleY(drivingTimeButtonScale).scaleX(drivingTimeButtonScale).translationY(drivingTimeButtonTranslationY).setInterpolator(Interpolators.linearOutSlowInInterpolator).duration = (if (animate) 700 else 0).toLong()

        vLocationBackground!!.animate().alpha(locationBackgroundAlpha).setInterpolator(Interpolators.linearOutSlowInInterpolator).setStartDelay(0).duration = (if (animate) 300 else 0).toLong()
        vgLocationContainer!!.animate().translationY(locationContainerTranslationY).scaleX(locationContainerScale).scaleY(locationContainerScale).setStartDelay(0).setInterpolator(Interpolators.linearOutSlowInInterpolator).duration = (if (animate) 300 else 0).toLong()

        ivDrivingIndicator!!.visibility = if (showResultsMode) View.VISIBLE else View.GONE

        if (restStopListFragment != null) {
            restStopListFragment!!.view!!.animate().alpha(resultsListAlpha).translationY(resultsListTranslationY).setInterpolator(Interpolators.linearOutSlowInInterpolator).setStartDelay((if (showResultsMode) 500 else 0).toLong()).setDuration((if (animate) 300 else 0).toLong()).withEndAction {
                if (!showResultsMode) {
                    clearSearchResults()
                }
            }
        }

        updateSearchBox()
    }

    override fun onDialogHmsSet(reference: Int, hours: Int, minutes: Int, seconds: Int) {
        drivingMinutesLeft = hours * 60 + minutes
        updateDrivingTimeLeftButton()
        loadSearchResults()
    }

    private fun updateDrivingTimeLeftButton() {
        val span = Spanny()
        val hours = drivingMinutesLeft / 60
        val minutes = drivingMinutesLeft % 60

        if (hours > 0) {
            span.append(hours.toString())
            span.append(" h", TypefaceSpan("sans-serif-light"))
            if (minutes > 0) {
                span.append(" ")
            }
        }

        if (minutes > 0) {
            span.append(minutes.toString())
            span.append(" m", TypefaceSpan("sans-serif-light"))
        }

        span.append(" driving time left", TypefaceSpan("sans-serif-light"))

        btDrivingTime!!.text = span
    }

    override fun onConnected(bundle: Bundle?) {
        getCurrentLocation()
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                googleApiClient,
                Cfg.ACTIVITY_DETECTION_INTERVAL,
                createActivityRecognitionIntentServicePendingIntent()).setResultCallback { status -> Log.d(TAG, "Added activity detection: " + status.statusMessage) }
    }

    private fun createActivityRecognitionIntentServicePendingIntent(): PendingIntent {
        return PendingIntent.getService(this, 0, Intent(this, ActivityRecognitionIntentService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_LOCATION)
            return
        }
        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        currentLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
        updateLocationContainer()
    }

    override fun onConnectionSuspended(i: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 112)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }

        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.errorCode)
        }
    }

    private fun getGooglePlacesAutocomplete(constraint: CharSequence): ArrayList<AutocompletePrediction>? {
        if (googleApiClient.isConnected) {
            Log.i(TAG, "Starting autocomplete query for: " + constraint)

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            val results = Places.GeoDataApi.getAutocompletePredictions(googleApiClient, constraint.toString(), null, null)
            //new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).build());

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            val autocompletePredictions = results.await(60, TimeUnit.SECONDS)

            // Confirm that the query completed successfully, otherwise return null
            val status = autocompletePredictions.status
            if (!status.isSuccess) {
                Toast.makeText(this, "Error contacting API: " + status.toString(),
                        Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString())
                autocompletePredictions.release()
                return null
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.count
                    + " predictions.")

            // Freeze the results immutable representation that can be stored safely.
            return DataBufferUtils.freezeAndClose(autocompletePredictions)
        }
        Log.e(TAG, "Google API client is not connected for autocomplete query.")
        return null
    }

    override fun onResult(places: PlaceBuffer) {
        if (!places.status.isSuccess) {
            // Request did not complete successfully
            Log.e(TAG, "Place query did not complete. Error: " + places.status.toString())
            places.release()
            return
        }
        // Get the Place object from the buffer.
        val place = places.get(0)
        Log.i(TAG, "Place details received: " + place.name)
        currentDestination = place.latLng
        loadSearchResults()

        places.release()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION) {
            getCurrentLocation()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onActivityDetectedEvent(event: ActivityDetectedEvent) {
        if (!event.detectedActivities.isEmpty()) {
            for (de in event.detectedActivities) {
                if ((de.type == DetectedActivity.IN_VEHICLE || de.type == DetectedActivity.ON_FOOT) && de.confidence > 15) {
                    ivDrivingIndicator!!.animate().alpha(1f)
                    return
                }
            }
        }
        ivDrivingIndicator!!.animate().alpha(0f)
    }

    override fun useEventBus(): Boolean {
        return true
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
        private val KEY_SEARCH_DESTINATION = "destination"
        private val KEY_SEARCH_SOURCE = "source"
        private val KEY_SEARCH_MINUTES_LEFT = "minutes_left"
        private val REQUEST_CODE_LOCATION = 202
    }
}

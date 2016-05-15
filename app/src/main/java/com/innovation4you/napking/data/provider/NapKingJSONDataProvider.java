package com.innovation4you.napking.data.provider;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.innovation4you.napking.app.Cfg;
import com.innovation4you.napking.model.RestStop;
import com.innovation4you.napking.model.SearchResult;
import com.innovation4you.napking.util.GPSUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class NapKingJSONDataProvider implements INapKingDataProvider {

	protected List<RestStop> restStops;

	public NapKingJSONDataProvider(final Context context) {
		loadData(context);
	}

	protected void loadData(final Context context) {
		final Gson gson = new GsonBuilder().create();
		final Type listType = new TypeToken<ArrayList<RestStop>>() {
		}.getType();
		try {
			restStops = gson.fromJson(new InputStreamReader(context.getAssets().open("data.json")), listType);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<RestStop> findRestStops(String condition) {
		condition = condition.toLowerCase();

		final List<RestStop> result = new ArrayList<>();
		for (RestStop restStop : restStops) {
			if (restStop.name.toLowerCase().contains(condition)) {
				result.add(restStop);
			}
		}
		return result;
	}

	@Override
	public List<SearchResult> search(LatLng source, LatLng destination, int minutesLeft) {
		final List<SearchResult> result = new ArrayList<>();
		Calendar now;
		double distanceFromSource, distanceToDestination;
		int drivingDuration;
		Log.d("Search", "from: " + source.toString() + " to: " + destination.toString());

		for (RestStop restStop : restStops) {
			distanceFromSource = GPSUtil.calculateDistance(restStop.lat, restStop.lng, source.latitude, source.longitude);
			drivingDuration = GPSUtil.claculateDrivingDuration(distanceFromSource, Cfg.AVG_DRIVING_SPEED);
			Log.d("Search", restStop.name + " " + distanceFromSource + "m " + drivingDuration + "min");

			if (drivingDuration <= minutesLeft) {
				distanceToDestination = GPSUtil.calculateDistance(restStop.lat, restStop.lng, destination.latitude, destination.longitude);

				now = new GregorianCalendar();
				now.add(Calendar.MINUTE, drivingDuration);
				result.add(new SearchResult(restStop.name, restStop.getOccupancyAtTime(now), (int) distanceFromSource,
						(int) distanceToDestination, drivingDuration, restStop.id));
			}
		}

		Collections.sort(result);
		return result;
	}

	@Override
	public RestStop findRestStopById(long id) {
		for (RestStop restStop : restStops) {
			if (restStop.id == id) {
				return restStop;
			}
		}
		return null;
	}
}

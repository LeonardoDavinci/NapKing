package com.innovation4you.napking.data.provider;

import android.content.Context;
import android.util.Log;

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
	public List<SearchResult> search(String condition, int minutesLeft, double currentLat, double currentLng) {
		final List<SearchResult> result = new ArrayList<>();
		Calendar now;
		double distance;
		int drivingDuration;

		for (RestStop restStop : findRestStops(condition)) {
			distance = GPSUtil.calculateDistance(restStop.lat, restStop.lng, currentLat, currentLng, 0, 0);
			drivingDuration = GPSUtil.claculateDrivingDuration(distance, Cfg.AVG_DRIVING_SPEED);
			Log.d("Search", restStop.name + " " + distance + "m " + drivingDuration + "min");
			if (drivingDuration <= minutesLeft) {
				now = new GregorianCalendar();
				now.add(Calendar.MINUTE, drivingDuration);
				result.add(new SearchResult(restStop.name, restStop.getOccupancyAtTime(now), (int) distance, drivingDuration, restStop
						.id));
			}
		}
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

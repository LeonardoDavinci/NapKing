package com.innovation4you.napking.data.provider;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.innovation4you.napking.model.RestStop;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NapKingJSONDataProvider implements INapKingDataProvider {

	public List<RestStop> restStops;

	public NapKingJSONDataProvider(final Context context) {
		loadData(context);
	}

	private void loadData(Context context) {
		final Gson gson = new GsonBuilder().create();
		final Type listType = new TypeToken<ArrayList<RestStop>>() {}.getType();
		try {
			restStops = gson.fromJson(new InputStreamReader(context.getAssets().open("data.json")), listType);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<RestStop> findRestStops(String condition) {
		return restStops;
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

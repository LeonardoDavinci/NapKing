package com.innovation4you.napking.data.provider;

import android.content.Context;

import com.innovation4you.napking.model.OccupancyEntry;
import com.innovation4you.napking.model.RestStop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NapKingStaticDataProvider extends NapKingJSONDataProvider {

	final static int NUMBER_OF_REST_STOPS = 10;
	Random random;

	public NapKingStaticDataProvider(Context context) {
		super(context);
	}

	@Override
	protected void loadData(Context context) {
		random = new Random();
		restStops = new ArrayList<>(NUMBER_OF_REST_STOPS);
		for (int i = 0; i < NUMBER_OF_REST_STOPS; i++) {
			restStops.add(createRestStop(i));
		}
	}

	private RestStop createRestStop(long id) {
		final List<OccupancyEntry> occupancies = new ArrayList<>();
		for (int w = 0; w < 7; w++) {
			for (int h = 0; h < 24; h++) {
				occupancies.add(new OccupancyEntry(w, h, random.nextInt(100)));
			}
		}
		return new RestStop(id, NAMES[random.nextInt(NAMES.length - 1)], occupancies, 48.220778, 16.3100205);
	}

	private static final String[] NAMES = new String[]{"Wildewyn", "Falconvale", "Goldmill", "Wyvernhollow", "Shadownesse", "Springhaven",
			"Corbourne", "Clearbourne", "Deepbush", "Blackford", "Wyvernwynne", "Stronghurst", "Icefall"};

}

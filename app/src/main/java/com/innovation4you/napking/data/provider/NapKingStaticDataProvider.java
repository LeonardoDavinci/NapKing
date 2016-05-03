package com.innovation4you.napking.data.provider;

import com.innovation4you.napking.model.OccupancyEntry;
import com.innovation4you.napking.model.RestStop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NapKingStaticDataProvider implements INapKingDataProvider {

	final Random occupancyRandom = new Random();

	@Override
	public List<RestStop> findRestStops(String condition) {
		final List<RestStop> restStops = new ArrayList<>();
		restStops.add(createRestStop());
		return restStops;
	}

	private RestStop createRestStop() {
		final List<OccupancyEntry> occupancies = new ArrayList<>();
		for (int w = 0; w < 7; w++) {
			for (int h = 0; h < 24; h++) {
				for (int m = 0; m < 1; m++) {
					occupancies.add(new OccupancyEntry(w, h, m, occupancyRandom.nextInt(100)));
				}
			}
		}
		return new RestStop("Braavos", occupancies);
	}
}

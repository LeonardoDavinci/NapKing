package com.innovation4you.napking.data.provider;

import com.innovation4you.napking.model.OccupancyEntry;
import com.innovation4you.napking.model.RestStop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NapKingStaticDataProvider implements INapKingDataProvider {

	final Random occupancyRandom = new Random();
	private List<RestStop> restStops = new ArrayList<>();

	public NapKingStaticDataProvider() {
		restStops.add(createRestStop());
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

	private RestStop createRestStop() {
		final List<OccupancyEntry> occupancies = new ArrayList<>();
		for (int w = 0; w < 7; w++) {
			for (int h = 0; h < 24; h++) {
				for (int m = 0; m < 1; m++) {
					occupancies.add(new OccupancyEntry(w, h, m, occupancyRandom.nextInt(100)));
				}
			}
		}
		return new RestStop(restStops.size(), "Braavos", occupancies, 40.7127837, -74.00594130000002);
	}
}

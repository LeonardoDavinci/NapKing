package com.innovation4you.napking.data;

import com.innovation4you.napking.data.provider.INapKingDataProvider;
import com.innovation4you.napking.model.RestStop;
import com.innovation4you.napking.model.SearchResult;

import java.util.List;

public class NapKingRepository implements INapKingDataProvider {

	private static NapKingRepository instance;

	private INapKingDataProvider dataProvider;

	public static void init(INapKingDataProvider dataProvider) {
		if (instance == null) {
			instance = new NapKingRepository(dataProvider);
		}
	}

	public static NapKingRepository get() {
		return instance;
	}

	private NapKingRepository(INapKingDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}


	@Override
	public List<RestStop> findRestStops(String condition) {
		return dataProvider.findRestStops(condition);
	}

	@Override
	public List<SearchResult> search(String condition, int minutesLeft, double currentLat, double currentLng) {
		return dataProvider.search(condition, minutesLeft, currentLat, currentLng);
	}

	@Override
	public RestStop findRestStopById(long id) {
		return dataProvider.findRestStopById(id);
	}
}
